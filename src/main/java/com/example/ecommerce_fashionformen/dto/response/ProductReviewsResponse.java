package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewsResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private int rating;
    private String title;
    private String comment;
}
