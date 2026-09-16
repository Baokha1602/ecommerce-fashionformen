package com.example.ecommerce_fashionformen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.momo")
public class MoMoProperties {
    private String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private String partnerCode = "SANDBOX";
    private String accessKey = "SANDBOX_KEY";
    private String secretKey = "SANDBOX_SECRET";
    private String returnUrl = "http://localhost:5173/payment/momo/return";
    private String ipnUrl = "http://localhost:8080/api/payments/momo/ipn";
}
