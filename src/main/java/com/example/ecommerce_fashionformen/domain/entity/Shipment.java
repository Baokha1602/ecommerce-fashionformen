package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Lưu thông tin vận đơn GHN sau khi Admin kích hoạt giao hàng.
 */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment extends AuditableEntity {

    /** ID đơn hàng liên kết */
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    /** Mã vận đơn do GHN cấp */
    @Column(name = "ghn_order_code", nullable = false, length = 100)
    private String ghnOrderCode;

    /**
     * Thời gian dự kiến giao hàng (chuỗi ISO-8601 trả về từ GHN).
     * VD: "2024-12-25T10:00:00+07:00"
     */
    @Column(name = "expected_delivery_time", length = 50)
    private String expectedDeliveryTime;

    /** Số tiền COD giao cho shipper thu (VNĐ) */
    @Column(name = "cod_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal codAmount = BigDecimal.ZERO;

    /** Cân nặng tổng kiện hàng (gram) */
    @Column(name = "total_weight")
    private Integer totalWeight;

    /** Phí vận chuyển thực tế GHN tính (VNĐ) */
    @Column(name = "total_fee", precision = 19, scale = 4)
    private BigDecimal totalFee;

    /** Toàn bộ JSON response từ GHN (để debug/audit) */
    @Column(name = "ghn_raw_response", columnDefinition = "TEXT")
    private String ghnRawResponse;
}
