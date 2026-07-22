package com.example.ecommerce_fashionformen.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private String appliedCouponCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemResponse> cartItems;
    private java.math.BigDecimal subtotal;
    private java.math.BigDecimal productDiscount;
    private java.math.BigDecimal couponDiscount;
    private java.math.BigDecimal rankDiscount;
    private java.math.BigDecimal shippingFee;
    private java.math.BigDecimal finalAmount;
}
