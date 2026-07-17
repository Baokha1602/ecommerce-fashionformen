package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantsResponse {
    private Long id;
    private Long productId;
    private String name;
    private java.math.BigDecimal price;
    private java.math.BigDecimal discountPrice;
    private java.math.BigDecimal discountRate;
    private int stockTotal;
    private int stockLock;
}
