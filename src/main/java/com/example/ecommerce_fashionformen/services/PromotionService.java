package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.promotion.PromotionCreateRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;
import java.util.List;

import com.example.ecommerce_fashionformen.dto.promotion.CouponResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);
    PromotionResponse updatePromotion(Long id, PromotionCreateRequest request);
    void deletePromotion(Long id);
    PromotionResponse getPromotion(Long id);
    List<PromotionResponse> getAllPromotions();
    CouponResponse validateCoupon(String couponCode);
}
