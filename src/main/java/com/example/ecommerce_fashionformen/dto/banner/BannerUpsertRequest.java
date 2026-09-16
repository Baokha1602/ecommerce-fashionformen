package com.example.ecommerce_fashionformen.dto.banner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerUpsertRequest {

    @Size(max = 150, message = "Tiêu đề không được vượt quá 150 ký tự")
    private String title;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    @Size(max = 500, message = "Đường dẫn hình ảnh không được vượt quá 500 ký tự")
    private String imageUrl;

    @Size(max = 500, message = "Đường dẫn liên kết không được vượt quá 500 ký tự")
    private String linkUrl;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer displayOrder;

    private Boolean isActive;
}
