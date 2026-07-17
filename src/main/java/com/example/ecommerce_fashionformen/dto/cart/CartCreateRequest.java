package com.example.ecommerce_fashionformen.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartCreateRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;
}
