package com.example.ecommerce_fashionformen.dto.otp;

import com.example.ecommerce_fashionformen.domain.enums.OtpPurpose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponse {

    private Long id;
    private String email;
    private OtpPurpose purpose;
    private LocalDateTime expiresAt;
    private Boolean isUsed;
    private int failedOtpAttempts;
    private String resetPasswordToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
