package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.order.PaymentUrlResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    PaymentUrlResponse createVnPayUrl(Long orderId, Long userId, HttpServletRequest request);
    PaymentUrlResponse createMoMoUrl(Long orderId, Long userId);
    Map<String, String> processVnPayReturn(Map<String, String> params);
    Map<String, String> processVnPayIpn(Map<String, String> params);
    Map<String, String> processMoMoIpn(Map<String, String> params);
}
