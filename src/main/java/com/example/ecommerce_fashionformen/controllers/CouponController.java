package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.coupon.CouponResponse;
import com.example.ecommerce_fashionformen.services.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer-facing coupon endpoints.
 * CRUD admin endpoints đã chuyển sang AdminCouponController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/available")
    public ApiResponse<List<CouponResponse>> getAvailableCoupons() {
        return ApiResponse.success("Lấy danh sách mã giảm giá khả dụng thành công",
                couponService.getAvailableCoupons());
    }

    @GetMapping("/code/{code}")
    public ApiResponse<CouponResponse> getByCode(@PathVariable String code) {
        return ApiResponse.success("Lấy mã giảm giá thành công", couponService.findByCode(code));
    }
}
