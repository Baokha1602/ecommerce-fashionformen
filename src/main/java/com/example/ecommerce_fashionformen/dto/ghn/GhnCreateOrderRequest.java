package com.example.ecommerce_fashionformen.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Payload gửi tới GHN API để tạo đơn vận chuyển.
 * Endpoint: POST /shipping-order/create
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GhnCreateOrderRequest {

    /** Tên người nhận */
    @JsonProperty("to_name")
    private String toName;

    /** Số điện thoại người nhận */
    @JsonProperty("to_phone")
    private String toPhone;

    /** Địa chỉ người nhận (số nhà, tên đường) */
    @JsonProperty("to_address")
    private String toAddress;

    /** Mã phường/xã GHN */
    @JsonProperty("to_ward_code")
    private String toWardCode;

    /** ID quận/huyện GHN */
    @JsonProperty("to_district_id")
    private Integer toDistrictId;

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

    /**
     * Loại dịch vụ:
     * 2 = Giao hàng chuẩn
     */
    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    /**
     * Hình thức thanh toán phí ship:
     * 1 = Người gửi/cửa hàng trả
     * 2 = Người nhận trả
     */
    @JsonProperty("payment_type_id")
    private Integer paymentTypeId;

    /**
     * Số tiền thu hộ COD (VNĐ).
     * = 0 nếu đã thanh toán online.
     */
    @JsonProperty("cod_amount")
    private Long codAmount;

    /**
     * Ghi chú cho shipper.
     * CHOTHUHANG = Cho xem hàng trước khi nhận
     * CHOXEMHANGKHONGTHU = Cho xem hàng, không thử
     * KHONGCHOXEMHANG = Không cho xem hàng
     */
    @JsonProperty("required_note")
    private String requiredNote;

    /** Danh sách sản phẩm */
    @JsonProperty("items")
    private List<GhnOrderItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GhnOrderItem {
        @JsonProperty("name")
        private String name;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("price")
        private Long price;
    }
}
