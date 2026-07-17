package com.example.ecommerce_fashionformen.dto.promotion;

import com.example.ecommerce_fashionformen.domain.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductResponse {
    private Long id;
    private Long promotionId;
    private Long productVariantId;
    private DiscountType discountType;
    private BigDecimal discountValue;
}
