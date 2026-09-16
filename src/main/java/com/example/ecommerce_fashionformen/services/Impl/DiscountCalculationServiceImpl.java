package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.promotion.DiscountResult;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.services.DiscountCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service tính giảm giá dùng chung cho cả Cart và Order,
 * tránh trùng lặp logic coupon/rank discount giữa 2 module.
 *
 * Rule: Nếu có couponCode hợp lệ → dùng coupon discount, KHÔNG dùng rank discount.
 *       Nếu không có couponCode → dùng rank discount.
 */
@Service
@RequiredArgsConstructor
public class DiscountCalculationServiceImpl implements DiscountCalculationService {

    private final CouponRepository couponRepository;

    @Override
    public DiscountResult calculate(BigDecimal subtotalAfterProductDiscount, String couponCode, Rank userRank) {
        BigDecimal couponDiscount = BigDecimal.ZERO;
        BigDecimal rankDiscount = BigDecimal.ZERO;
        Long appliedCouponId = null;
        String errorMessage = null;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            // Có mã coupon → validate và tính coupon discount
            Optional<Coupon> couponOpt = couponRepository.findByCode(couponCode.trim());

            if (couponOpt.isEmpty()) {
                errorMessage = "Mã giảm giá không tồn tại: " + couponCode;
            } else {
                Coupon coupon = couponOpt.get();

                if (!Boolean.TRUE.equals(coupon.getIsActive())) {
                    errorMessage = "Mã giảm giá đã bị vô hiệu hóa";
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
                        errorMessage = "Mã giảm giá đã hết hạn hoặc chưa đến ngày áp dụng";
                    } else if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
                        errorMessage = "Mã giảm giá đã hết lượt sử dụng";
                    } else if (subtotalAfterProductDiscount.compareTo(coupon.getMinOrderValue()) < 0) {
                        errorMessage = "Giá trị đơn hàng tối thiểu để áp dụng mã giảm giá là "
                                + coupon.getMinOrderValue().setScale(0, RoundingMode.HALF_UP) + " VNĐ";
                    } else {
                        // Coupon hợp lệ → tính discount
                        couponDiscount = subtotalAfterProductDiscount
                                .multiply(coupon.getDiscountRate())
                                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                        if (coupon.getMaxDiscountAmount() != null
                                && couponDiscount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                            couponDiscount = coupon.getMaxDiscountAmount();
                        }

                        appliedCouponId = coupon.getId();
                    }
                }
            }
        } else {
            // Không có coupon → áp dụng rank discount nếu có
            if (userRank != null && userRank.getRankDiscount() != null
                    && userRank.getRankDiscount().compareTo(BigDecimal.ZERO) > 0) {
                rankDiscount = subtotalAfterProductDiscount
                        .multiply(userRank.getRankDiscount())
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            }
        }

        return DiscountResult.builder()
                .couponDiscount(couponDiscount)
                .rankDiscount(rankDiscount)
                .appliedCouponId(appliedCouponId)
                .errorMessage(errorMessage)
                .build();
    }
}
