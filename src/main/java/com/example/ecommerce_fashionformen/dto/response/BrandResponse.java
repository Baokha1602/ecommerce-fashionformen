package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandResponse {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isActive;
}
