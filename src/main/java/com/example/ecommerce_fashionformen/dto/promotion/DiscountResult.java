package com.example.ecommerce_fashionformen.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResult {
    private BigDecimal couponDiscount;
    private BigDecimal rankDiscount;
    private Long appliedCouponId;
    private String errorMessage;

    public boolean isSuccess() {
        return errorMessage == null || errorMessage.isEmpty();
    }
}
