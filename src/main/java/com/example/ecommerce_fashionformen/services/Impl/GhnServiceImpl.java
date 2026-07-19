package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.config.GhnConfig;
import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.entity.OrderItem;
import com.example.ecommerce_fashionformen.domain.entity.UserAddress;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderRequest;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderRequest.GhnOrderItem;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderResponse;
import com.example.ecommerce_fashionformen.dto.ghn.GhnFeeRequest;
import com.example.ecommerce_fashionformen.dto.ghn.GhnFeeResponse;
import com.example.ecommerce_fashionformen.services.GhnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GhnServiceImpl implements GhnService {

    // ── Quy định kích thước mặc định cho 1 sản phẩm thời trang ──────────────
    private static final int ITEM_WEIGHT_GRAMS = 300;   // gram
    private static final int ITEM_HEIGHT_CM    = 10;    // cm
    private static final int ITEM_LENGTH_CM    = 30;    // cm
    private static final int ITEM_WIDTH_CM     = 25;    // cm

    /** Phí vận chuyển mặc định dùng khi GHN không phản hồi (fallback) */
    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("30000");

    /** service_type_id = 2 → Giao hàng chuẩn */
    private static final int SERVICE_TYPE_STANDARD = 2;

    /** payment_type_id = 1 → Người gửi/cửa hàng trả phí ship */
    private static final int PAYMENT_TYPE_SENDER = 1;

    private final GhnConfig ghnConfig;
    private final RestTemplate restTemplate;

    // ─────────────────────────────────────────────────────────────────────────
    // Tính phí vận chuyển
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public BigDecimal calculateShippingFee(int totalQuantity, int districtId, String wardCode) {
        try {
            GhnFeeRequest payload = GhnFeeRequest.builder()
                    .serviceTypeId(SERVICE_TYPE_STANDARD)
                    .toDistrictId(districtId)
                    .toWardCode(wardCode)
                    .weight(ITEM_WEIGHT_GRAMS * totalQuantity)
                    .length(ITEM_LENGTH_CM)
                    .width(ITEM_WIDTH_CM)
                    .height(ITEM_HEIGHT_CM * totalQuantity)
                    .build();

            HttpEntity<GhnFeeRequest> request = new HttpEntity<>(payload, buildHeaders());
            String url = ghnConfig.getBaseUrl() + "/shipping-order/fee";

            log.info("[GHN] Tính phí vận chuyển → districtId={}, wardCode={}, qty={}",
                    districtId, wardCode, totalQuantity);

            ResponseEntity<GhnFeeResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, GhnFeeResponse.class);

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getData() != null
                    && response.getBody().getData().getTotal() != null) {

                BigDecimal fee = BigDecimal.valueOf(response.getBody().getData().getTotal());
                log.info("[GHN] Phí vận chuyển = {} VNĐ", fee);
                return fee;
            }

            log.warn("[GHN] Phản hồi không hợp lệ khi tính phí. Dùng phí mặc định.");
            return DEFAULT_SHIPPING_FEE;

        } catch (RestClientException ex) {
            log.error("[GHN] Lỗi kết nối khi tính phí: {}. Dùng phí mặc định.", ex.getMessage());
            return DEFAULT_SHIPPING_FEE;
        } catch (Exception ex) {
            log.error("[GHN] Lỗi không xác định khi tính phí: {}", ex.getMessage(), ex);
            return DEFAULT_SHIPPING_FEE;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tạo đơn vận chuyển GHN
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public GhnCreateOrderResponse createShippingOrder(Order order,
                                                      UserAddress address,
                                                      List<OrderItem> orderItems) {
        int totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // Tính COD: nếu đã thanh toán online → COD = 0
        long codAmount = Boolean.TRUE.equals(order.getIsPaid())
                ? 0L
                : order.getFinalAmount().longValue();

        // Chuẩn bị danh sách item cho GHN
        List<GhnOrderItem> ghnItems = orderItems.stream()
                .map(item -> GhnOrderItem.builder()
                        .name("Product Variant #" + item.getProductVariantId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice().longValue())
                        .build())
                .collect(Collectors.toList());

        GhnCreateOrderRequest payload = GhnCreateOrderRequest.builder()
                .toName((order.getFirstName() + " " + order.getLastName()).trim())
                .toPhone(order.getPhoneNumber())
                .toAddress(address.getAddress())
                .toWardCode(address.getWardId())
                .toDistrictId(address.getDistrictId().intValue())
                .weight(ITEM_WEIGHT_GRAMS * totalQuantity)
                .length(ITEM_LENGTH_CM)
                .width(ITEM_WIDTH_CM)
                .height(ITEM_HEIGHT_CM * totalQuantity)
                .serviceTypeId(SERVICE_TYPE_STANDARD)
                .paymentTypeId(PAYMENT_TYPE_SENDER)
                .codAmount(codAmount)
                .requiredNote("CHOTHUHANG")
                .items(ghnItems)
                .build();

        HttpEntity<GhnCreateOrderRequest> request = new HttpEntity<>(payload, buildHeaders());
        String url = ghnConfig.getBaseUrl() + "/shipping-order/create";

        log.info("[GHN] Tạo đơn vận chuyển → orderId={}, toPhone={}, codAmount={}",
                order.getId(), order.getPhoneNumber(), codAmount);

        ResponseEntity<GhnCreateOrderResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, GhnCreateOrderResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("[GHN] Tạo đơn vận chuyển thất bại: không có phản hồi");
        }

        GhnCreateOrderResponse body = response.getBody();
        if (body.getCode() == null || body.getCode() != 200) {
            throw new RuntimeException("[GHN] Tạo đơn vận chuyển thất bại: " + body.getMessage());
        }

        log.info("[GHN] Tạo đơn thành công → ghnOrderCode={}, expectedDelivery={}",
                body.getData().getOrderCode(),
                body.getData().getExpectedDeliveryTime());

        return body;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tạo HttpHeaders chứa GHN Token và ShopId.
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", ghnConfig.getApiToken());
        headers.set("ShopId", ghnConfig.getShopId());
        return headers;
    }
}
