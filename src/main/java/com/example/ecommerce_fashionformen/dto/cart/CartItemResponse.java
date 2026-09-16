package com.example.ecommerce_fashionformen.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long cartId;
    private Long productVariantId;
    private String productVariantName;
    private Integer quantity;
    private java.math.BigDecimal price;
    private java.math.BigDecimal discountPrice;
    private Boolean unavailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
