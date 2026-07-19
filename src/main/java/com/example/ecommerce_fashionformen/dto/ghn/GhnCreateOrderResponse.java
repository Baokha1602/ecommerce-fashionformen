package com.example.ecommerce_fashionformen.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response từ GHN API khi tạo đơn vận chuyển.
 * Endpoint: POST /shipping-order/create
 */
@Data
@NoArgsConstructor
public class GhnCreateOrderResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private GhnCreateOrderData data;

    @Data
    @NoArgsConstructor
    public static class GhnCreateOrderData {
        /** Mã vận đơn GHN */
        @JsonProperty("order_code")
        private String orderCode;

        /** Thời gian dự kiến giao hàng (ISO 8601) */
        @JsonProperty("expected_delivery_time")
        private String expectedDeliveryTime;

        /** Phí vận chuyển thực tế */
        @JsonProperty("total_fee")
        private Long totalFee;

        /** Trạng thái vận đơn */
        @JsonProperty("status")
        private String status;
    }
}
