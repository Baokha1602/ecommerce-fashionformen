package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.ConflictException;
import com.example.ecommerce_fashionformen.domain.entity.RefreshTokenSession;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.auth.AuthResponse;
import com.example.ecommerce_fashionformen.dto.auth.RefreshTokenRequest;
import com.example.ecommerce_fashionformen.dto.user.UserLoginRequest;
import com.example.ecommerce_fashionformen.dto.user.UserRegisterRequest;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import com.example.ecommerce_fashionformen.repository.RefreshTokenSessionRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.security.JwtTokenProvider;
import com.example.ecommerce_fashionformen.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;



    @Override
    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        // 1. Kiểm tra trùng lặp dữ liệu
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã được sử dụng");
        }

        // 2. Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setUserRole(UserRole.CUSTOMER);
        user.setIsActive(true);
        user.setCurrentPoint(0);

        user = userRepository.save(user);

        // 3. Tạo token và trả về
        return buildAuthResponse(user);
    }



    @Override
    @Transactional
    public AuthResponse login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Tên đăng nhập hoặc mật khẩu không đúng"));

        // 2. Kiểm tra tài khoản có bị khóa không
        if (!user.getIsActive()) {
            throw new BadRequestException("Tài khoản đã bị khóa");
        }

        // 3. So sánh mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        // 4. Tạo token và trả về
        return buildAuthResponse(user);
    }



    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        // 1. Validate refresh token
        String jti;
        Long userId;
        try {
            jti = jwtTokenProvider.getJtiFromToken(token);
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // 2. Tìm session trong DB
        RefreshTokenSession session = refreshTokenSessionRepository.findByJti(jti)
                .orElseThrow(() -> new BadRequestException("Refresh token không tồn tại"));

        // 3. Kiểm tra session chưa bị revoke
        if (session.getRevokedAt() != null) {
            throw new BadRequestException("Refresh token đã bị thu hồi");
        }

        // 4. Kiểm tra chưa hết hạn
        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token đã hết hạn");
        }

        // 5. Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Tài khoản đã bị khóa");
        }

        // 6. Revoke session cũ
        String newJti = jwtTokenProvider.generateJti();
        session.setRevokedAt(Instant.now());
        session.setReplacedByJti(newJti);
        refreshTokenSessionRepository.save(session);

        // 7. Tạo token mới
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenNew = jwtTokenProvider.generateRefreshToken(user, newJti);

        // 8. Lưu session mới
        saveRefreshTokenSession(user, newJti);

        return new AuthResponse(accessToken, refreshTokenNew, mapToUserResponse(user));
    }


    private AuthResponse buildAuthResponse(User user) {
        String jti = jwtTokenProvider.generateJti();
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user, jti);

        saveRefreshTokenSession(user, jti);

        return new AuthResponse(accessToken, refreshToken, mapToUserResponse(user));
    }


    private void saveRefreshTokenSession(User user, String jti) {
        RefreshTokenSession session = new RefreshTokenSession();
        session.setJti(jti);
        session.setUser(user);
        session.setExpiresAt(jwtTokenProvider.getRefreshTokenExpiry());
        refreshTokenSessionRepository.save(session);
    }


    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setFullName(user.getFullName());
        response.setAvatarImage(user.getAvatarImage());
        response.setCurrentPoint(user.getCurrentPoint());
        response.setUserRole(user.getUserRole());
        response.setIsActive(user.getIsActive());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRankName(user.getRank() != null ? user.getRank().getRankName().name() : null);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
