package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.promotion.PromotionCreateRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionProductRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;

import java.math.BigDecimal;
import java.util.List;

import com.example.ecommerce_fashionformen.dto.promotion.CouponResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);
    PromotionResponse updatePromotion(Long id, PromotionCreateRequest request);
    void deletePromotion(Long id);
    PromotionResponse getPromotion(Long id);
    List<PromotionResponse> getAllPromotions();
    List<PromotionResponse> getActivePromotions();

    // Bulk add/remove sản phẩm khuyến mãi
    PromotionResponse addPromotionProducts(Long promotionId, List<PromotionProductRequest> products);
    PromotionResponse removePromotionProducts(Long promotionId, List<Long> variantIds);

    // Validate coupon với orderAmount để trả lại số tiền giảm thực tế
    CouponResponse validateCoupon(String couponCode, BigDecimal orderAmount);
}
