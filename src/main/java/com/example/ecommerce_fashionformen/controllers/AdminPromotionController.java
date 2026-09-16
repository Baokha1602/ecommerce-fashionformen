package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionCreateRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionProductRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;
import com.example.ecommerce_fashionformen.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ApiResponse<List<PromotionResponse>> getAllPromotions() {
        return ApiResponse.success("Lấy danh sách khuyến mãi thành công",
                promotionService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionResponse> getPromotion(@PathVariable Long id) {
        return ApiResponse.success("Lấy chi tiết khuyến mãi thành công",
                promotionService.getPromotion(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(
            @RequestBody @Valid PromotionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo khuyến mãi thành công",
                        promotionService.createPromotion(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<PromotionResponse> updatePromotion(@PathVariable Long id,
                                                           @RequestBody @Valid PromotionCreateRequest request) {
        return ApiResponse.success("Cập nhật khuyến mãi thành công",
                promotionService.updatePromotion(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ApiResponse.successMessage("Xóa khuyến mãi thành công");
    }

    @PostMapping("/{id}/products")
    public ApiResponse<PromotionResponse> addProducts(@PathVariable Long id,
                                                       @RequestBody @Valid List<PromotionProductRequest> products) {
        return ApiResponse.success("Thêm sản phẩm khuyến mãi thành công",
                promotionService.addPromotionProducts(id, products));
    }

    @DeleteMapping("/{id}/products")
    public ApiResponse<PromotionResponse> removeProducts(@PathVariable Long id,
                                                          @RequestBody List<Long> variantIds) {
        return ApiResponse.success("Gỡ sản phẩm khuyến mãi thành công",
                promotionService.removePromotionProducts(id, variantIds));
    }
}
