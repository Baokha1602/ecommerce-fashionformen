package com.example.ecommerce_fashionformen.controller;

import com.example.ecommerce_fashionformen.dto.promotion.PromotionCreateRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;
import com.example.ecommerce_fashionformen.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@RequestBody @Valid PromotionCreateRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Long id, @RequestBody @Valid PromotionCreateRequest request) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotion(id));
    }

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/validate")
    public ResponseEntity<com.example.ecommerce_fashionformen.dto.promotion.CouponResponse> validateCoupon(@RequestParam String couponCode) {
        return ResponseEntity.ok(promotionService.validateCoupon(couponCode));
    }
}
