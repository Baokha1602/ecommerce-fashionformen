package com.example.ecommerce_fashionformen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.vnpay")
public class VnPayProperties {
    private String url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private String tmnCode = "SANDBOX";
    private String hashSecret = "SANDBOX_SECRET";
    private String returnUrl = "http://localhost:5173/payment/vnpay/return";
    private String ipnUrl = "http://localhost:8080/api/payments/vnpay/ipn";
}
