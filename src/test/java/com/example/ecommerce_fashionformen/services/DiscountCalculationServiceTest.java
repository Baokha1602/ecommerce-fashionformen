package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.promotion.DiscountResult;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.services.Impl.DiscountCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountCalculationServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DiscountCalculationServiceImpl discountCalculationService;

    private Coupon validCoupon;
    private Rank goldRank;

    @BeforeEach
    void setUp() {
        validCoupon = new Coupon();
        validCoupon.setId(1L);
        validCoupon.setCode("SALE10");
        validCoupon.setDiscountRate(new BigDecimal("10.00"));
        validCoupon.setMaxDiscountAmount(new BigDecimal("50000"));
        validCoupon.setMinOrderValue(new BigDecimal("100000"));
        validCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        validCoupon.setEndDate(LocalDateTime.now().plusDays(30));
        validCoupon.setUsageLimit(10);
        validCoupon.setIsActive(true);

        goldRank = new Rank();
        goldRank.setId(3L);
        goldRank.setRankDiscount(new BigDecimal("5.00"));
        goldRank.setPoint(2000);
    }

    @Test
    @DisplayName("Tính coupon discount đúng với rate 10% và cap 50000")
    void calculate_WithValidCoupon_ShouldReturnCouponDiscount() {
        BigDecimal subtotal = new BigDecimal("300000");
        when(couponRepository.findByCode("SALE10")).thenReturn(Optional.of(validCoupon));

        DiscountResult result = discountCalculationService.calculate(subtotal, "SALE10", goldRank);

        assertThat(result.isSuccess()).isTrue();
        // 300000 * 10% = 30000 (< cap 50000, nên dùng 30000)
        assertThat(result.getCouponDiscount()).isEqualByComparingTo(new BigDecimal("30000.0000"));
        assertThat(result.getRankDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getAppliedCouponId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Coupon discount bị cap bởi maxDiscountAmount")
    void calculate_WithCouponExceedingCap_ShouldCapAtMaxDiscountAmount() {
        BigDecimal subtotal = new BigDecimal("1000000"); // 1M
        when(couponRepository.findByCode("SALE10")).thenReturn(Optional.of(validCoupon));

        DiscountResult result = discountCalculationService.calculate(subtotal, "SALE10", goldRank);

        assertThat(result.isSuccess()).isTrue();
        // 1000000 * 10% = 100000 > cap 50000, nên dùng 50000
        assertThat(result.getCouponDiscount()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(result.getRankDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Không có coupon → dùng rank discount")
    void calculate_WithNoCoupon_ShouldReturnRankDiscount() {
        BigDecimal subtotal = new BigDecimal("500000");

        DiscountResult result = discountCalculationService.calculate(subtotal, null, goldRank);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getCouponDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        // 500000 * 5% = 25000
        assertThat(result.getRankDiscount()).isEqualByComparingTo(new BigDecimal("25000.0000"));
        assertThat(result.getAppliedCouponId()).isNull();
    }

    @Test
    @DisplayName("Coupon hết hạn → trả error message")
    void calculate_WithExpiredCoupon_ShouldReturnError() {
        validCoupon.setEndDate(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCode("SALE10")).thenReturn(Optional.of(validCoupon));

        DiscountResult result = discountCalculationService.calculate(new BigDecimal("300000"), "SALE10", goldRank);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("hết hạn");
        assertThat(result.getCouponDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Coupon hết usage limit → trả error message")
    void calculate_WithZeroUsageLimit_ShouldReturnError() {
        validCoupon.setUsageLimit(0);
        when(couponRepository.findByCode("SALE10")).thenReturn(Optional.of(validCoupon));

        DiscountResult result = discountCalculationService.calculate(new BigDecimal("300000"), "SALE10", goldRank);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("hết lượt");
    }

    @Test
    @DisplayName("Coupon không tồn tại → trả error message")
    void calculate_WithNonExistentCoupon_ShouldReturnError() {
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        DiscountResult result = discountCalculationService.calculate(new BigDecimal("300000"), "INVALID", goldRank);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("không tồn tại");
    }

    @Test
    @DisplayName("Đơn hàng chưa đạt giá trị tối thiểu → trả error")
    void calculate_WithOrderBelowMinValue_ShouldReturnError() {
        BigDecimal subtotal = new BigDecimal("50000"); // < minOrderValue 100000
        when(couponRepository.findByCode("SALE10")).thenReturn(Optional.of(validCoupon));

        DiscountResult result = discountCalculationService.calculate(subtotal, "SALE10", goldRank);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("tối thiểu");
    }

    @Test
    @DisplayName("Không có coupon và không có rank → discount = 0")
    void calculate_WithNoCouponAndNoRank_ShouldReturnZero() {
        BigDecimal subtotal = new BigDecimal("300000");

        DiscountResult result = discountCalculationService.calculate(subtotal, null, null);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getCouponDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getRankDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
