package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.otp.OtpResponse;
import com.example.ecommerce_fashionformen.dto.otp.OtpSendRequest;
import com.example.ecommerce_fashionformen.dto.otp.OtpVerifyRequest;
import com.example.ecommerce_fashionformen.dto.otp.ResetPasswordRequest;

public interface OtpService {
    void sendOtpForPasswordReset(OtpSendRequest request);
    OtpResponse verifyOtpForPasswordReset(OtpVerifyRequest request);
    void resetPassword(ResetPasswordRequest request);
}
