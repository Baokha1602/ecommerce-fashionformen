package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.cart.CartItemRequest;
import com.example.ecommerce_fashionformen.dto.cart.CartResponse;

public interface CartService {
    CartResponse getCartDetails(Long userId);
    CartResponse addCartItem(Long userId, CartItemRequest request);
    CartResponse updateCartItem(Long userId, Long cartItemId, CartItemRequest request);
    CartResponse removeCartItem(Long userId, Long cartItemId);
    CartResponse applyCoupon(Long userId, String couponCode);
    CartResponse removeCoupon(Long userId);
    void clearCart(Long userId);
}
