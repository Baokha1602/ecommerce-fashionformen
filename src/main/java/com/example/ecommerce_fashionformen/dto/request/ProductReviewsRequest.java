package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewsRequest {
    private Long productId;
    private Long userId;
    private int rating;
    private String title;
    private String comment;
}
