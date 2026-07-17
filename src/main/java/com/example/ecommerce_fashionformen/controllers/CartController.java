package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.dto.cart.CartItemRequest;
import com.example.ecommerce_fashionformen.dto.cart.CartResponse;
import com.example.ecommerce_fashionformen.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCartDetails(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartDetails(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addCartItem(@RequestParam Long userId, @RequestBody @Valid CartItemRequest request) {
        return ResponseEntity.ok(cartService.addCartItem(userId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(@RequestParam Long userId, @PathVariable Long cartItemId, @RequestBody @Valid CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@RequestParam Long userId, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeCartItem(userId, cartItemId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartResponse> applyCoupon(@RequestParam Long userId, @RequestParam String couponCode) {
        return ResponseEntity.ok(cartService.applyCoupon(userId, couponCode));
    }
}
