package com.example.ecommerce_fashionformen.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload gửi tới GHN API để tính phí vận chuyển.
 * Endpoint: POST /shipping-order/fee
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GhnFeeRequest {

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    /** Cân nặng tổng (gram) */
    @JsonProperty("weight")
    private Integer weight;

    /** Chiều dài tổng (cm) */
    @JsonProperty("length")
    private Integer length;

    /** Chiều rộng tổng (cm) */
    @JsonProperty("width")
    private Integer width;

    /** Chiều cao tổng (cm) */
    @JsonProperty("height")
    private Integer height;
}
