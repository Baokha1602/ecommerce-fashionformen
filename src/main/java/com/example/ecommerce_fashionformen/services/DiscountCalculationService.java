package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.promotion.DiscountResult;

import java.math.BigDecimal;

public interface DiscountCalculationService {
    DiscountResult calculate(BigDecimal subtotalAfterProductDiscount, String couponCode, Rank userRank);
}
