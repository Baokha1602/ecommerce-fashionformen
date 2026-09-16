package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private Long categoryId;
    private Long brandId;
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
}
