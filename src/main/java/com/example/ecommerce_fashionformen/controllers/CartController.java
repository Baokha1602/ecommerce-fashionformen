package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.cart.CartItemRequest;
import com.example.ecommerce_fashionformen.dto.cart.CartResponse;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<CartResponse> getCartDetails() {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Lấy giỏ hàng thành công", cartService.getCartDetails(userId));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addCartItem(@RequestBody @Valid CartItemRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Thêm sản phẩm vào giỏ thành công", cartService.addCartItem(userId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> updateCartItem(@PathVariable Long cartItemId,
                                                     @RequestBody @Valid CartItemRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Cập nhật giỏ hàng thành công",
                cartService.updateCartItem(userId, cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> removeCartItem(@PathVariable Long cartItemId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Xóa sản phẩm khỏi giỏ thành công",
                cartService.removeCartItem(userId, cartItemId));
    }

    @PostMapping("/coupon")
    public ApiResponse<CartResponse> applyCoupon(@RequestParam String couponCode) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Áp dụng mã giảm giá thành công",
                cartService.applyCoupon(userId, couponCode));
    }

    @DeleteMapping("/coupon")
    public ApiResponse<CartResponse> removeCoupon() {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Gỡ mã giảm giá thành công", cartService.removeCoupon(userId));
    }

    @DeleteMapping
    public ApiResponse<Void> clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
        return ApiResponse.successMessage("Xóa toàn bộ giỏ hàng thành công");
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getId();
    }
}
