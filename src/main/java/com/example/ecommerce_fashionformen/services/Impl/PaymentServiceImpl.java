package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.config.VnPayProperties;
import com.example.ecommerce_fashionformen.config.MoMoProperties;
import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.entity.OrderItem;
import com.example.ecommerce_fashionformen.domain.entity.OrderStatusHistory;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.dto.order.PaymentUrlResponse;
import com.example.ecommerce_fashionformen.repository.OrderItemRepository;
import com.example.ecommerce_fashionformen.repository.OrderRepository;
import com.example.ecommerce_fashionformen.repository.OrderStatusHistoryRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.services.NotificationService;
import com.example.ecommerce_fashionformen.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantsRepository productVariantsRepository;
    private final VnPayProperties vnPayProperties;
    private final MoMoProperties moMoProperties;
    private final NotificationService notificationService;

    // Self-inject qua @Lazy để fix lỗi self-proxy (giúp @Transactional hoạt động đúng)
    @Autowired
    @Lazy
    private PaymentService self;

    // Timeout tối thiểu 30s theo yêu cầu MoMo
    private final RestTemplate restTemplate = buildRestTemplate();

    private static final long MOMO_MIN_AMOUNT = 1_000L;
    private static final long MOMO_MAX_AMOUNT = 50_000_000L;

    private static final DateTimeFormatter VN_PAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(30_000);
        return new RestTemplate(factory);
    }

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

        // Validate amount theo giới hạn MoMo: 1.000 - 50.000.000 VND
        if (amount < MOMO_MIN_AMOUNT || amount > MOMO_MAX_AMOUNT) {
            throw new BadRequestException(
                    "Số tiền thanh toán MoMo phải từ 1.000 đến 50.000.000 VND (hiện tại: " + amount + " VND)");
        }

        String requestId = UUID.randomUUID().toString();
        String momoOrderId = "MOMO_" + orderId + "_" + System.currentTimeMillis();
        String orderInfo = "Thanh toan don hang #" + orderId;
        String extraData = "";
        String requestType = "captureWallet";

        // Build raw signature theo thứ tự alphabet của MoMo
        String rawSignature = "accessKey=" + moMoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + moMoProperties.getIpnUrl()
                + "&orderId=" + momoOrderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + moMoProperties.getPartnerCode()
                + "&redirectUrl=" + moMoProperties.getReturnUrl()
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = hmacSHA256(moMoProperties.getSecretKey(), rawSignature);

        // Gửi kèm thông tin user để hiển thị trên trang thanh toán MoMo
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", order.getFirstName() + " " + order.getLastName());
        userInfo.put("phoneNumber", order.getPhoneNumber());
        userInfo.put("email", order.getEmail() != null ? order.getEmail() : "");

        // Đóng gói request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", moMoProperties.getPartnerCode());
        requestBody.put("accessKey", moMoProperties.getAccessKey());
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", momoOrderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", moMoProperties.getReturnUrl());
        requestBody.put("ipnUrl", moMoProperties.getIpnUrl());
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("userInfo", userInfo);
        requestBody.put("signature", signature);
        requestBody.put("lang", "vi");

        // Gọi HTTP POST tới API MoMo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    moMoProperties.getEndpoint(), entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new RuntimeException("MoMo không trả về response body");
            }

            String resultCode = String.valueOf(responseBody.get("resultCode"));
            if (!"0".equals(resultCode)) {
                throw new RuntimeException("MoMo lỗi [" + resultCode + "]: " + responseBody.get("message"));
            }

            String payUrl = String.valueOf(responseBody.get("payUrl"));
            log.info("MoMo payment URL tạo thành công cho đơn #{}: {}", orderId, payUrl);

            return PaymentUrlResponse.builder()
                    .orderId(orderId)
                    .paymentUrl(payUrl)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khởi tạo thanh toán MoMo cho đơn #{}: {}", orderId, e.getMessage());
            throw new RuntimeException("Không thể khởi tạo thanh toán MoMo: " + e.getMessage(), e);
        }
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

    // ==================== MoMo IPN (server-to-server) ====================

    // Xử lý IPN MoMo: Không bắt Exception ở đây để @Transactional rollback khi có lỗi DB.
    @Override
    @Transactional
    public Map<String, String> processMoMoIpn(Map<String, Object> params) {
        Map<String, String> result = new HashMap<>();

        // Parse các field từ IPN body
        String orderId      = String.valueOf(params.get("orderId"));
        String requestId    = String.valueOf(params.get("requestId"));
        String amount       = String.valueOf(params.get("amount"));
        String orderInfo    = String.valueOf(params.get("orderInfo"));
        String orderType    = String.valueOf(params.get("orderType"));
        String transId      = String.valueOf(params.get("transId"));
        String resultCode   = String.valueOf(params.get("resultCode"));
        String message      = String.valueOf(params.get("message"));
        String payType      = String.valueOf(params.get("payType"));
        String responseTime = String.valueOf(params.get("responseTime"));
        String extraData    = String.valueOf(params.get("extraData"));
        String receivedSig  = String.valueOf(params.get("signature"));

        // 1. Tạo rawSignature IPN theo thứ tự alphabet bắt buộc của MoMo
        String rawSignature = "accessKey=" + moMoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + moMoProperties.getPartnerCode()
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;

        // 2. Verify chữ ký HMAC-SHA256
        String expectedSig = hmacSHA256(moMoProperties.getSecretKey(), rawSignature);
        if (!expectedSig.equals(receivedSig)) {
            log.warn("[MoMo IPN] Chữ ký không hợp lệ! orderId={}", orderId);
            result.put("status", "INVALID_SIGNATURE");
            result.put("message", "Chữ ký không hợp lệ");
            return result;
        }

        // 3. Tách real orderId từ format MOMO_{id}_{timestamp}
        String realOrderId = extractOrderIdFromMoMo(orderId);
        Long orderIdLong = Long.parseLong(realOrderId);

        // 4. Xử lý kết quả - exception được để bay ra để @Transactional rollback
        if ("0".equals(resultCode)) {
            updateOrderPaymentSuccess(orderIdLong, "MoMo IPN");
            result.put("status", "SUCCESS");
            result.put("message", "Thanh toán thành công");
            log.info("[MoMo IPN] Thanh toán thành công. orderId={}, transId={}", orderId, transId);
        } else {
            // resultCode != 0: thanh toán thất bại / hết hạn / bị hủy
            log.warn("[MoMo IPN] Thanh toán thất bại. orderId={}, resultCode={}, message={}",
                    orderId, resultCode, message);
            updateOrderPaymentFailed(orderIdLong, "MoMo IPN", "MoMo resultCode=" + resultCode + ": " + message);
            result.put("status", "FAILED");
            result.put("message", "Thanh toán MoMo thất bại. Mã lỗi: " + resultCode);
        }

        return result;
    }

    // ==================== MoMo Redirect Return (browser) ====================

    // Xử lý redirect return của MoMo: Gọi qua proxy "self" để @Transactional của processMoMoIpn hoạt động đúng.
    @Override
    public String processMoMoReturn(Map<String, String> params) {
        String resultCode = params.get("resultCode");

        if ("0".equals(resultCode)) {
            // Fallback: cập nhật DB nếu IPN chưa đến được (môi trường localhost)
            // Gọi qua self (Spring proxy) để @Transactional hoạt động đúng
            try {
                Map<String, Object> returnData = new HashMap<>(params);
                self.processMoMoIpn(returnData);
            } catch (Exception e) {
                log.warn("[MoMo Return Fallback] Lỗi fallback update DB: {}", e.getMessage());
            }

            return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Thanh toán MoMo</title>"
                    + "<style>body{font-family:sans-serif;display:flex;justify-content:center;"
                    + "align-items:center;height:100vh;margin:0;background:#f0fdf4;}"
                    + ".card{background:#fff;border-radius:12px;padding:40px;text-align:center;"
                    + "box-shadow:0 4px 20px rgba(0,0,0,0.1);}"
                    + "h1{color:#16a34a;} p{color:#555;}</style></head>"
                    + "<body><div class=\"card\"><h1>&#10003; Thanh toán thành công!</h1>"
                    + "<p>Cảm ơn bạn đã thanh toán qua MoMo. Đơn hàng của bạn đang được xử lý.</p>"
                    + "</div></body></html>";
        } else {
            // Thất bại / hủy / hết hạn QR - cũng fallback hủy đơn nếu cần
            try {
                Map<String, Object> returnData = new HashMap<>(params);
                self.processMoMoIpn(returnData);
            } catch (Exception e) {
                log.warn("[MoMo Return Fallback] Lỗi khi xử lý failed return: {}", e.getMessage());
            }

            return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Thanh toán MoMo</title>"
                    + "<style>body{font-family:sans-serif;display:flex;justify-content:center;"
                    + "align-items:center;height:100vh;margin:0;background:#fef2f2;}"
                    + ".card{background:#fff;border-radius:12px;padding:40px;text-align:center;"
                    + "box-shadow:0 4px 20px rgba(0,0,0,0.1);}"
                    + "h1{color:#dc2626;} p{color:#555;}</style></head>"
                    + "<body><div class=\"card\"><h1>&#10007; Thanh toán thất bại!</h1>"
                    + "<p>Thanh toán bị hủy hoặc hết hạn. Mã lỗi: " + resultCode + "</p>"
                    + "</div></body></html>";
        }
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

    // Cập nhật đơn hàng thành công, dùng Pessimistic Lock chống race condition
    private void updateOrderPaymentSuccess(Long orderId, String source) {
        Order order = orderRepository.findByIdWithLock(orderId).orElse(null);
        if (order == null || Boolean.TRUE.equals(order.getIsPaid())) {
            log.info("[{}] Đơn #{} đã được xử lý trước đó, bỏ qua.", source, orderId);
            return;
        }

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

        try {
            notificationService.notifyOrderPaymentSuccess(orderId, source);
        } catch (Exception e) {
            log.warn("[NOTIFICATION] Không thể gửi thông báo thanh toán đơn #{}: {}", orderId, e.getMessage());
        }
    }

    // Cập nhật đơn hàng thất bại/hủy/hết hạn QR, dùng Pessimistic Lock và hoàn tồn kho
    private void updateOrderPaymentFailed(Long orderId, String source, String reason) {
        Order order = orderRepository.findByIdWithLock(orderId).orElse(null);
        if (order == null) {
            log.warn("[{}] Không tìm thấy đơn #{} để xử lý thất bại.", source, orderId);
            return;
        }

        // Chỉ xử lý nếu đơn vẫn đang PENDING và chưa thanh toán
        if (order.getOrderStatus() != OrderStatus.PENDING || Boolean.TRUE.equals(order.getIsPaid())) {
            log.info("[{}] Đơn #{} không ở trạng thái chờ, bỏ qua xử lý thất bại. status={}",
                    source, orderId, order.getOrderStatus());
            return;
        }

        // Cập nhật trạng thái đơn hàng → CANCELLED
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .changedBy("SYSTEM (" + source + ")")
                .changedAt(LocalDateTime.now())
                .note("Thanh toán thất bại/hết hạn: " + reason)
                .build();
        orderStatusHistoryRepository.save(history);

        // Hoàn tồn kho: cộng lại stockTotal, trừ stockLock
        restoreStockForOrder(orderId, source);

        log.info("[{}] Đơn #{} đã bị hủy và hoàn kho. Lý do: {}", source, orderId, reason);
    }

    // Hoàn tồn kho cho các sản phẩm trong đơn hàng
    private void restoreStockForOrder(Long orderId, String source) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            productVariantsRepository.findByIdForUpdate(item.getProductVariantId())
                    .ifPresent(variant -> {
                        int qty = item.getQuantity();
                        // Hoàn tồn kho thực tế
                        variant.setStockTotal(variant.getStockTotal() + qty);
                        // Giải phóng kho đang khóa (nếu có)
                        int newLock = Math.max(0, variant.getStockLock() - qty);
                        variant.setStockLock(newLock);
                        productVariantsRepository.save(variant);
                        log.info("[{}] Hoàn kho variantId={}, +{} units", source,
                                item.getProductVariantId(), qty);
                    });
        }
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
