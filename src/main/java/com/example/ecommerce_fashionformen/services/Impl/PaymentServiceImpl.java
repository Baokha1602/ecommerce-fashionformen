package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.config.VnPayProperties;
import com.example.ecommerce_fashionformen.config.MoMoProperties;
import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.entity.OrderStatusHistory;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.dto.order.PaymentUrlResponse;
import com.example.ecommerce_fashionformen.repository.OrderRepository;
import com.example.ecommerce_fashionformen.repository.OrderStatusHistoryRepository;
import com.example.ecommerce_fashionformen.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final VnPayProperties vnPayProperties;
    private final MoMoProperties moMoProperties;

    private static final DateTimeFormatter VN_PAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public PaymentUrlResponse createVnPayUrl(Long orderId, Long userId, HttpServletRequest request) {
        Order order = getOrderForPayment(orderId, userId, PaymentMethod.VN_PAY);

        long amount = order.getFinalAmount().longValue() * 100; // VNPay yêu cầu nhân 100

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(orderId));
        params.put("vnp_OrderInfo", "Thanh toan don hang #" + orderId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_CreateDate", LocalDateTime.now().format(VN_PAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(15).format(VN_PAY_DATE_FORMAT));

        // Build query string + hash
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append("&");
                query.append("&");
            }
            hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
            query.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }

        String secureHash = hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        String paymentUrl = vnPayProperties.getUrl() + "?" + query;

        return PaymentUrlResponse.builder()
                .orderId(orderId)
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public PaymentUrlResponse createMoMoUrl(Long orderId, Long userId) {
        Order order = getOrderForPayment(orderId, userId, PaymentMethod.MOMO);

        long amount = order.getFinalAmount().longValue();
        String requestId = UUID.randomUUID().toString();
        String momoOrderId = "MOMO_" + orderId + "_" + System.currentTimeMillis();

        // Build raw signature theo tài liệu MoMo
        String rawSignature = "accessKey=" + moMoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData="
                + "&ipnUrl=" + moMoProperties.getIpnUrl()
                + "&orderId=" + momoOrderId
                + "&orderInfo=Thanh toan don hang #" + orderId
                + "&partnerCode=" + moMoProperties.getPartnerCode()
                + "&redirectUrl=" + moMoProperties.getReturnUrl()
                + "&requestId=" + requestId
                + "&requestType=captureWallet";

        String signature = hmacSHA256(moMoProperties.getSecretKey(), rawSignature);

        // Trong thực tế sẽ gọi API MoMo để lấy payUrl
        // Ở đây trả về placeholder URL do cần tài khoản sandbox thực
        String paymentUrl = moMoProperties.getEndpoint()
                + "?partnerCode=" + moMoProperties.getPartnerCode()
                + "&orderId=" + momoOrderId
                + "&amount=" + amount
                + "&requestId=" + requestId;

        log.info("MoMo payment URL created for order #{}, signature: {}", orderId, signature);

        return PaymentUrlResponse.builder()
                .orderId(orderId)
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> processVnPayReturn(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();

        String vnpSecureHash = params.get("vnp_SecureHash");
        Map<String, String> fields = new TreeMap<>(params);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (hashData.length() > 0) hashData.append("&");
            hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }

        String calculatedHash = hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());

        if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
            result.put("status", "FAILED");
            result.put("message", "Chữ ký không hợp lệ");
            return result;
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            updateOrderPaymentSuccess(Long.parseLong(txnRef), "VNPay");
            result.put("status", "SUCCESS");
            result.put("message", "Thanh toán thành công");
        } else {
            result.put("status", "FAILED");
            result.put("message", "Thanh toán thất bại. Mã lỗi: " + responseCode);
        }

        result.put("orderId", txnRef);
        return result;
    }

    @Override
    @Transactional
    public Map<String, String> processVnPayIpn(Map<String, String> params) {
        // Logic tương tự processVnPayReturn nhưng trả RspCode cho VNPay server
        Map<String, String> result = new HashMap<>();

        String vnpSecureHash = params.get("vnp_SecureHash");
        Map<String, String> fields = new TreeMap<>(params);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (hashData.length() > 0) hashData.append("&");
            hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }

        String calculatedHash = hmacSHA512(vnPayProperties.getHashSecret(), hashData.toString());

        if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
            result.put("RspCode", "97");
            result.put("Message", "Invalid Checksum");
            return result;
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        try {
            if ("00".equals(responseCode)) {
                updateOrderPaymentSuccess(Long.parseLong(txnRef), "VNPay IPN");
                result.put("RspCode", "00");
                result.put("Message", "Confirm Success");
            } else {
                result.put("RspCode", "00");
                result.put("Message", "Confirm Success");
            }
        } catch (Exception e) {
            result.put("RspCode", "99");
            result.put("Message", "Unknown error");
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, String> processMoMoIpn(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();

        String resultCode = params.get("resultCode");
        String orderInfo = params.get("orderInfo");

        // Extract orderId từ orderInfo
        try {
            if ("0".equals(resultCode)) {
                // Thanh toán thành công
                String orderIdStr = extractOrderIdFromMoMo(params.get("orderId"));
                updateOrderPaymentSuccess(Long.parseLong(orderIdStr), "MoMo IPN");
                result.put("status", "SUCCESS");
            } else {
                result.put("status", "FAILED");
                result.put("message", "Thanh toán MoMo thất bại. Mã lỗi: " + resultCode);
            }
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Lỗi xử lý IPN: " + e.getMessage());
        }

        return result;
    }

    // ==================== Helper Methods ====================

    private Order getOrderForPayment(Long orderId, Long userId, PaymentMethod expectedMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thanh toán đơn hàng này");
        }

        if (order.getPaymentMethod() != expectedMethod) {
            throw new BadRequestException("Phương thức thanh toán của đơn hàng không phải " + expectedMethod.name());
        }

        if (Boolean.TRUE.equals(order.getIsPaid())) {
            throw new BadRequestException("Đơn hàng đã được thanh toán");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        return order;
    }

    private void updateOrderPaymentSuccess(Long orderId, String source) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || Boolean.TRUE.equals(order.getIsPaid())) return;

        order.setIsPaid(true);
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PROCESSING)
                .changedBy("SYSTEM (" + source + ")")
                .changedAt(LocalDateTime.now())
                .note("Thanh toán online thành công, tự động chuyển sang PROCESSING")
                .build();
        orderStatusHistoryRepository.save(history);

        log.info("Đơn #{} thanh toán thành công qua {}", orderId, source);
    }

    private String extractOrderIdFromMoMo(String momoOrderId) {
        // Format: MOMO_{orderId}_{timestamp}
        if (momoOrderId != null && momoOrderId.startsWith("MOMO_")) {
            String[] parts = momoOrderId.split("_");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return momoOrderId;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC-SHA512", e);
        }
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC-SHA256", e);
        }
    }
}
