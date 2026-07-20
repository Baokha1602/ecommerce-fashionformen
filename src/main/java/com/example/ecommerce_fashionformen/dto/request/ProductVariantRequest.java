package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    @NotBlank(message = "Tên biến thể không được để trống")
    private String name;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Giá giảm không được âm")
    private BigDecimal discountPrice;

    @Min(value = 0, message = "Tỷ lệ giảm giá không được âm")
    private BigDecimal discountRate;

    @Min(value = 0, message = "Tổng tồn kho không được âm")
    private Integer stockTotal;

    @Min(value = 0, message = "Tồn kho giữ không được âm")
    private Integer stockLock;

    private List<Long> attributeValueIds;
}
