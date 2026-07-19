package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.auth.AuthResponse;
import com.example.ecommerce_fashionformen.dto.auth.RefreshTokenRequest;
import com.example.ecommerce_fashionformen.dto.user.UserLoginRequest;
import com.example.ecommerce_fashionformen.dto.user.UserRegisterRequest;
import com.example.ecommerce_fashionformen.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody UserRegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String refreshToken = getRefreshToken(request);
        
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token không tồn tại trong cookie", null));
        }
        
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);
        
        AuthResponse authResponse = authService.refreshToken(refreshRequest);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        
        return ResponseEntity.ok(ApiResponse.success("Làm mới token thành công", authResponse));
    }

    private String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // false for development on localhost
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
