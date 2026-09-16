package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductTagResponse {
    private Long id;
    private Long productId;
    private Long tagId;
}
