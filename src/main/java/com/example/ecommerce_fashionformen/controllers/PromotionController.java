package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.promotion.CouponResponse;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;
import com.example.ecommerce_fashionformen.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Public endpoints cho khách hàng: xem khuyến mãi đang chạy, validate coupon.
 * CRUD admin endpoints đã chuyển sang AdminPromotionController.
 */
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/active")
    public ApiResponse<List<PromotionResponse>> getActivePromotions() {
        return ApiResponse.success("Lấy danh sách khuyến mãi đang chạy thành công",
                promotionService.getActivePromotions());
    }

    @GetMapping("/validate")
    public ApiResponse<CouponResponse> validateCoupon(
            @RequestParam String couponCode,
            @RequestParam(required = false) BigDecimal orderAmount) {
        return ApiResponse.success("Kiểm tra mã giảm giá thành công",
                promotionService.validateCoupon(couponCode, orderAmount));
    }
}
