package com.example.ecommerce_fashionformen.dto.tag;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagUpdateRequest {

    @Size(max = 255, message = "Tên thẻ tag không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 255, message = "Mô tả thẻ tag không được vượt quá 255 ký tự")
    private String description;
}
