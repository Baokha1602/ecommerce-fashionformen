package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.coupon.CouponCreateRequest;
import com.example.ecommerce_fashionformen.dto.coupon.CouponResponse;
import com.example.ecommerce_fashionformen.dto.coupon.CouponUpdateRequest;
import com.example.ecommerce_fashionformen.dto.coupon.CouponUsageHistoryResponse;

import java.util.List;

public interface CouponService {

    List<CouponResponse> findAll();

    CouponResponse findById(Long id);

    CouponResponse findByCode(String code);

    CouponResponse create(CouponCreateRequest request);

    CouponResponse update(Long id, CouponUpdateRequest request);

    void delete(Long id);

    List<CouponResponse> getAvailableCoupons();

    List<CouponUsageHistoryResponse> getCouponUsageHistory(Long couponId);
}
