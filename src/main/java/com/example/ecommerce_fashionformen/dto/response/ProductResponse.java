package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private int soldQuantity;
}
