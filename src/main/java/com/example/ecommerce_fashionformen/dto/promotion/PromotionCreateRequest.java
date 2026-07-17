package com.example.ecommerce_fashionformen.dto.promotion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCreateRequest {
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    private Boolean isActive;

    @Valid
    private List<PromotionProductRequest> promotionProducts;
}
