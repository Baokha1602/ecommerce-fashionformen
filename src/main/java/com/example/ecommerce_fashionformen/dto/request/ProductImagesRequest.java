package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesRequest {
    private Long productId;
    @NotBlank(message = "Image URL is required")
    private String image;
    private boolean isMainImage;
}
