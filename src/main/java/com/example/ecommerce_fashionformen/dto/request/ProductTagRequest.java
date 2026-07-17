package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductTagRequest {
    private Long productId;
    private Long tagId;
}
