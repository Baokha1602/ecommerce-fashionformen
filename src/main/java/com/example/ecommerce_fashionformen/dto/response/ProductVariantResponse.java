package com.example.ecommerce_fashionformen.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String name;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal discountRate;
    private Integer stockTotal;
    private Integer stockLock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
