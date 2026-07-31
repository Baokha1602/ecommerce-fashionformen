package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.order.PaymentUrlResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    PaymentUrlResponse createVnPayUrl(Long orderId, Long userId, HttpServletRequest request);
    PaymentUrlResponse createMoMoUrl(Long orderId, Long userId);
    Map<String, String> processVnPayReturn(Map<String, String> params);
    Map<String, String> processVnPayIpn(Map<String, String> params);

    /**
     * Xử lý IPN từ MoMo (server-to-server).
     * Tách exception ra khỏi try-catch để @Transactional có thể rollback đúng.
     * Exception sẽ được bắt ở tầng Controller để đảm bảo luôn trả HTTP 200 cho MoMo.
     */
    Map<String, String> processMoMoIpn(Map<String, Object> params);

    /**
     * Xử lý Redirect Return từ MoMo (browser redirect).
     * Gọi processMoMoIpn qua Spring proxy (inject self) để @Transactional hoạt động đúng.
     */
    String processMoMoReturn(Map<String, String> params);
}


