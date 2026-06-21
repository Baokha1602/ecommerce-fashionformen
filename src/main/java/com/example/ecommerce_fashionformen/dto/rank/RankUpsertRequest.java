package com.example.ecommerce_fashionformen.dto.rank;

import com.example.ecommerce_fashionformen.domain.enums.RankName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankUpsertRequest {

    @NotNull(message = "Tên hạng không được để trống")
    private RankName rankName;

    @NotNull(message = "Điểm tích lũy tối thiểu không được để trống")
    @Min(value = 0, message = "Điểm tích lũy tối thiểu không được âm")
    private Integer point;

    @NotNull(message = "Giảm giá hạng không được để trống")
    @Min(value = 0, message = "Giảm giá hạng không được âm")
    @Max(value = 100, message = "Giảm giá hạng tối đa là 100%")
    private Double rankDiscount;
}
