package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantsRequest {
    private Long productId;
    @NotBlank(message = "Variant name is required")
    private String name;
    private java.math.BigDecimal price;
    private java.math.BigDecimal discountPrice;
    private java.math.BigDecimal discountRate;
    private int stockTotal;
    private int stockLock;
}
