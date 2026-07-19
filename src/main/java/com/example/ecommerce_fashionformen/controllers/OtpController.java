package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.otp.OtpResponse;
import com.example.ecommerce_fashionformen.dto.otp.OtpSendRequest;
import com.example.ecommerce_fashionformen.dto.otp.OtpVerifyRequest;
import com.example.ecommerce_fashionformen.dto.otp.ResetPasswordRequest;
import com.example.ecommerce_fashionformen.services.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        otpService.sendOtpForPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.successMessage("Mã OTP đã được gửi về email của bạn"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<OtpResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        OtpResponse otpResponse = otpService.verifyOtpForPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Xác thực OTP thành công", otpResponse));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        otpService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.successMessage("Đặt lại mật khẩu thành công"));
    }
}
