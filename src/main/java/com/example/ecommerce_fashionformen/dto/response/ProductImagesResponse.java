package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesResponse {
    private Long id;
    private Long productId;
    private String image;
    private boolean isMainImage;
}
