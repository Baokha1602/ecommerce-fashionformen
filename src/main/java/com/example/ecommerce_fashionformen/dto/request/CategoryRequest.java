package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    private Long parentId;
}
