package com.example.ecommerce_fashionformen.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response từ GHN API khi tính phí vận chuyển.
 * Endpoint: POST /shipping-order/fee
 */
@Data
@NoArgsConstructor
public class GhnFeeResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private GhnFeeData data;

    @Data
    @NoArgsConstructor
    public static class GhnFeeData {
        /** Tổng phí vận chuyển (VNĐ) */
        @JsonProperty("total")
        private Long total;

        @JsonProperty("service_fee")
        private Long serviceFee;

        @JsonProperty("insurance_fee")
        private Long insuranceFee;

        @JsonProperty("pick_station_fee")
        private Long pickStationFee;

        @JsonProperty("coupon_value")
        private Long couponValue;

        @JsonProperty("r2s_fee")
        private Long r2sFee;
    }
}
