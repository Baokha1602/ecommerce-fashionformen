package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Otp;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.OtpPurpose;
import com.example.ecommerce_fashionformen.dto.otp.OtpResponse;
import com.example.ecommerce_fashionformen.dto.otp.OtpSendRequest;
import com.example.ecommerce_fashionformen.dto.otp.OtpVerifyRequest;
import com.example.ecommerce_fashionformen.dto.otp.ResetPasswordRequest;
import com.example.ecommerce_fashionformen.repository.OtpRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.MailService;
import com.example.ecommerce_fashionformen.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void sendOtpForPasswordReset(OtpSendRequest request) {
        // 1. Kiểm tra email có tồn tại trong hệ thống không
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new NotFoundException("Email không tồn tại trên hệ thống");
        }

        // 2. Tạo mã OTP ngẫu nhiên 6 chữ số
        String otpCode = generateOtpCode();

        // 3. Tạo đối tượng Otp mới và lưu vào cơ sở dữ liệu
        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setOtpCode(otpCode);
        otp.setPurpose(OtpPurpose.RESET_PASSWORD);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Hết hạn sau 5 phút
        otp.setIsUsed(false);
        otp.setFailedOtpAttempts(0);

        otpRepository.save(otp);

        // 4. Gửi email chứa OTP cho người dùng
        String subject = "[Fashion For Man] Mã OTP khôi phục mật khẩu";
        String htmlBody = "<html><body>" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                "<h2 style='color: #333333; text-align: center;'>Yêu Cầu Đặt Lại Mật Khẩu</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu cho tài khoản liên kết với email này. Vui lòng sử dụng mã xác thực (OTP) dưới đây để tiếp tục:</p>" +
                "<div style='background-color: #f7f9fa; border: 1px dashed #cccccc; padding: 15px; text-align: center; margin: 20px 0;'>" +
                "<span style='font-size: 28px; font-weight: bold; letter-spacing: 4px; color: #007bff;'>" + otpCode + "</span>" +
                "</div>" +
                "<p style='color: #ff0000; font-weight: bold;'>Mã OTP này có hiệu lực trong vòng 5 phút.</p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bảo mật tài khoản và bỏ qua email này.</p>" +
                "<hr style='border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #888888; text-align: center;'>Email này được gửi tự động. Vui lòng không trả lời.</p>" +
                "</div>" +
                "</body></html>";

        mailService.sendEmail(request.getEmail(), subject, htmlBody, true);
    }

    @Override
    @Transactional
    public OtpResponse verifyOtpForPasswordReset(OtpVerifyRequest request) {
        // 1. Lấy mã OTP chưa sử dụng gần nhất của email này cho mục đích khôi phục mật khẩu
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(request.getEmail(), OtpPurpose.RESET_PASSWORD)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã OTP hợp lệ"));

        // 2. Kiểm tra xem OTP đã được sử dụng chưa
        if (Boolean.TRUE.equals(otp.getIsUsed())) {
            throw new BadRequestException("Mã OTP này đã được sử dụng trước đó");
        }

        // 3. Kiểm tra mã OTP có bị khóa do thử sai quá nhiều không
        if (otp.getFailedOtpAttempts() >= 5) {
            throw new BadRequestException("Mã OTP này đã bị vô hiệu hóa do thử sai quá 5 lần");
        }

        // 4. Kiểm tra xem mã OTP đã hết hạn chưa
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        // 5. Kiểm tra tính chính xác của OTP
        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            int attempts = otp.getFailedOtpAttempts() + 1;
            otp.setFailedOtpAttempts(attempts);
            otpRepository.save(otp);

            int remaining = 5 - attempts;
            if (remaining <= 0) {
                throw new BadRequestException("Mã OTP đã bị vô hiệu hóa do thử sai quá 5 lần");
            } else {
                throw new BadRequestException("Mã OTP không chính xác. Bạn còn " + remaining + " lần thử.");
            }
        }

        // 6. OTP chính xác: sinh token đặt lại mật khẩu
        String resetToken = UUID.randomUUID().toString();
        otp.setResetPasswordToken(resetToken);
        otp.setIsUsed(true);
        Otp savedOtp = otpRepository.save(otp);

        // 7. Map sang OtpResponse
        return mapToResponse(savedOtp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Kiểm tra hai mật khẩu có trùng khớp không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        // 2. Tìm OTP dựa trên token đặt lại mật khẩu
        Otp otp = otpRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new NotFoundException("Token đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng"));

        // 3. Kiểm tra xem token này có hết hạn chưa (hết hạn sau 15 phút tính từ lúc xác thực OTP thành công)
        if (otp.getUpdatedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token đặt lại mật khẩu đã hết hạn");
        }

        // 4. Cập nhật mật khẩu cho User liên kết với email của OTP
        User user = userRepository.findByEmail(otp.getEmail())
                .orElseThrow(() -> new NotFoundException("Người dùng liên kết với email này không tồn tại"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 5. Hủy token sau khi đã đổi mật khẩu thành công để tránh tái sử dụng
        otp.setResetPasswordToken(null);
        otpRepository.save(otp);
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private OtpResponse mapToResponse(Otp otp) {
        OtpResponse response = new OtpResponse();
        response.setId(otp.getId());
        response.setEmail(otp.getEmail());
        response.setPurpose(otp.getPurpose());
        response.setExpiresAt(otp.getExpiresAt());
        response.setIsUsed(otp.getIsUsed());
        response.setFailedOtpAttempts(otp.getFailedOtpAttempts());
        response.setResetPasswordToken(otp.getResetPasswordToken());
        response.setCreatedAt(otp.getCreatedAt());
        response.setUpdatedAt(otp.getUpdatedAt());
        return response;
    }
}
