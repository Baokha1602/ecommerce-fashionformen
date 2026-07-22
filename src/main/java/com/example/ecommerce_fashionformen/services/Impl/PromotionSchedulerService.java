package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import com.example.ecommerce_fashionformen.domain.entity.PromotionProduct;
import com.example.ecommerce_fashionformen.domain.enums.DiscountType;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.repository.PromotionProductRepository;
import com.example.ecommerce_fashionformen.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Job chạy định kỳ mỗi 10 phút để:
 * 1. Kích hoạt promotions đã đến startDate
 * 2. Deactivate promotions quá endDate → reset giá variant
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionSchedulerService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductVariantsRepository productVariantRepository;

    @Scheduled(fixedRate = 600000) // 10 phút
    @Transactional
    public void syncPromotionStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Kích hoạt promotions đã đến ngày bắt đầu
        List<Promotion> pendingPromotions = promotionRepository.findPendingPromotions(now);
        for (Promotion promotion : pendingPromotions) {
            promotion.setIsActive(true);
            promotionRepository.save(promotion);

            // Đồng bộ giá xuống variant
            List<PromotionProduct> products = promotionProductRepository.findByPromotionId(promotion.getId());
            for (PromotionProduct pp : products) {
                ProductVariant variant = productVariantRepository.findById(pp.getProductVariantId()).orElse(null);
                if (variant != null) {
                    applyDiscount(variant, pp.getDiscountType(), pp.getDiscountValue());
                    productVariantRepository.save(variant);
                }
            }

            log.info("Đã kích hoạt khuyến mãi: {} (ID: {})", promotion.getName(), promotion.getId());
        }

        // 2. Deactivate promotions hết hạn
        List<Promotion> expiredPromotions = promotionRepository.findExpiredActivePromotions(now);
        for (Promotion promotion : expiredPromotions) {
            promotion.setIsActive(false);
            promotionRepository.save(promotion);

            // Reset giá variant
            List<PromotionProduct> products = promotionProductRepository.findByPromotionId(promotion.getId());
            for (PromotionProduct pp : products) {
                ProductVariant variant = productVariantRepository.findById(pp.getProductVariantId()).orElse(null);
                if (variant != null) {
                    variant.setDiscountPrice(null);
                    variant.setDiscountRate(null);
                    productVariantRepository.save(variant);
                }
            }

            log.info("Đã kết thúc khuyến mãi: {} (ID: {})", promotion.getName(), promotion.getId());
        }
    }

    private void applyDiscount(ProductVariant variant, DiscountType type, BigDecimal value) {
        if (type == DiscountType.PERCENTAGE) {
            BigDecimal discountAmount = variant.getPrice().multiply(value)
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            variant.setDiscountPrice(variant.getPrice().subtract(discountAmount));
            variant.setDiscountRate(value);
        } else if (type == DiscountType.FIXED) {
            BigDecimal discountPrice = variant.getPrice().subtract(value);
            if (discountPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountPrice = BigDecimal.ZERO;
            }
            variant.setDiscountPrice(discountPrice);
            if (variant.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                variant.setDiscountRate(value.multiply(new BigDecimal("100"))
                        .divide(variant.getPrice(), 2, RoundingMode.HALF_UP));
            }
        }
    }
}
