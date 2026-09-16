package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.auth.AuthResponse;
import com.example.ecommerce_fashionformen.dto.auth.RefreshTokenRequest;
import com.example.ecommerce_fashionformen.dto.user.UserLoginRequest;
import com.example.ecommerce_fashionformen.dto.user.UserRegisterRequest;

public interface AuthService {

    AuthResponse register(UserRegisterRequest request);

    AuthResponse login(UserLoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
