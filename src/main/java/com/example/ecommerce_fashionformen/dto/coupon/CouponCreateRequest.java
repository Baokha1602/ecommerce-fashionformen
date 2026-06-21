package com.example.ecommerce_fashionformen.dto.coupon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponCreateRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(max = 50, message = "Mã giảm giá tối đa 50 ký tự")
    private String code;

    @NotBlank(message = "Tên coupon không được để trống")
    @Size(max = 255, message = "Tên coupon tối đa 255 ký tự")
    private String name;

    @NotNull(message = "Tỷ lệ giảm giá không được để trống")
    @Min(value = 0, message = "Tỷ lệ giảm giá không được nhỏ hơn 0")
    @Max(value = 100, message = "Tỷ lệ giảm giá tối đa là 100%")
    private Double discountRate;

    @Min(value = 0, message = "Số tiền giảm tối đa không được nhỏ hơn 0")
    private Double maxDiscountAmount;

    @NotNull(message = "Giá trị đơn hàng tối thiểu không được để trống")
    @Min(value = 0, message = "Giá trị đơn hàng tối thiểu không được nhỏ hơn 0")
    private Double minOrderValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @NotNull(message = "Giới hạn số lần sử dụng không được để trống")
    @Min(value = 1, message = "Giới hạn số lần sử dụng tối thiểu phải là 1")
    private Integer usageLimit;

    private Boolean isActive = true;
}
