package com.example.ecommerce_fashionformen.dto.auth;

import com.example.ecommerce_fashionformen.dto.user.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, UserResponse user) {
        this(accessToken, refreshToken, "Bearer", user);
    }
}
