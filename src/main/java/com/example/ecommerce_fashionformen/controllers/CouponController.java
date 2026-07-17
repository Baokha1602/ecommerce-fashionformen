package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.coupon.CouponCreateRequest;
import com.example.ecommerce_fashionformen.dto.coupon.CouponResponse;
import com.example.ecommerce_fashionformen.dto.coupon.CouponUpdateRequest;
import com.example.ecommerce_fashionformen.services.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<CouponResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách mã giảm giá thành công", couponService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CouponResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy mã giảm giá thành công", couponService.findById(id));
    }

    @GetMapping("/code/{code}")
    public ApiResponse<CouponResponse> getByCode(@PathVariable String code) {
        return ApiResponse.success("Lấy mã giảm giá thành công", couponService.findByCode(code));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo mã giảm giá thành công", couponService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<CouponResponse> update(@PathVariable Long id, @Valid @RequestBody CouponUpdateRequest request) {
        return ApiResponse.success("Cập nhật mã giảm giá thành công", couponService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ApiResponse.successMessage("Xóa mã giảm giá thành công");
    }
}
