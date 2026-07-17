package com.example.ecommerce_fashionformen.dto.promotion;

import com.example.ecommerce_fashionformen.domain.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductRequest {
    @NotNull(message = "Mã biến thể sản phẩm không được để trống")
    private Long productVariantId;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", message = "Giá trị giảm phải lớn hơn hoặc bằng 0")
    private BigDecimal discountValue;
}
