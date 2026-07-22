package com.example.ecommerce_fashionformen.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsageHistoryResponse {
    private Long id;
    private Long couponId;
    private String couponCode;
    private Long orderId;
    private Long userId;
    private LocalDateTime usedAt;
}
