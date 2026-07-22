package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import com.example.ecommerce_fashionformen.domain.entity.PromotionProduct;
import com.example.ecommerce_fashionformen.domain.enums.DiscountType;
import com.example.ecommerce_fashionformen.dto.promotion.*;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.repository.PromotionProductRepository;
import com.example.ecommerce_fashionformen.repository.PromotionRepository;
import com.example.ecommerce_fashionformen.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        Promotion promotion = mapper.map(request, Promotion.class);
        promotion = promotionRepository.save(promotion);

        if (request.getPromotionProducts() != null && !request.getPromotionProducts().isEmpty()) {
            Promotion finalPromotion = promotion;
            List<PromotionProduct> products = request.getPromotionProducts().stream().map(p -> {
                PromotionProduct pp = mapper.map(p, PromotionProduct.class);
                pp.setPromotion(finalPromotion);
                return pp;
            }).collect(Collectors.toList());
            promotionProductRepository.saveAll(products);

            // Tự động đồng bộ discountPrice/discountRate xuống ProductVariant
            if (Boolean.TRUE.equals(promotion.getIsActive()) && isPromotionCurrentlyActive(promotion)) {
                syncVariantDiscounts(products);
            }
        }

        return mapper.map(promotion, PromotionResponse.class);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionCreateRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));

        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        if (request.getIsActive() != null) {
            promotion.setIsActive(request.getIsActive());
        }

        promotion = promotionRepository.save(promotion);

        // Nếu deactivate → reset giá các variant liên quan
        if (!Boolean.TRUE.equals(promotion.getIsActive())) {
            resetVariantDiscountsForPromotion(id);
        }

        return mapper.map(promotion, PromotionResponse.class);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));

        // Reset giá variant trước khi xóa
        resetVariantDiscountsForPromotion(id);
        promotionProductRepository.deleteByPromotionId(id);
        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionResponse getPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));
        PromotionResponse response = mapper.map(promotion, PromotionResponse.class);
        List<PromotionProduct> products = promotionProductRepository.findByPromotionId(id);
        response.setPromotionProducts(products.stream()
                .map(p -> mapper.map(p, PromotionProductResponse.class))
                .collect(Collectors.toList()));
        return response;
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(p -> {
                    PromotionResponse resp = mapper.map(p, PromotionResponse.class);
                    List<PromotionProduct> products = promotionProductRepository.findByPromotionId(p.getId());
                    resp.setPromotionProducts(products.stream()
                            .map(pp -> mapper.map(pp, PromotionProductResponse.class))
                            .collect(Collectors.toList()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findActivePromotions(now).stream()
                .map(p -> {
                    PromotionResponse resp = mapper.map(p, PromotionResponse.class);
                    List<PromotionProduct> products = promotionProductRepository.findByPromotionId(p.getId());
                    resp.setPromotionProducts(products.stream()
                            .map(pp -> mapper.map(pp, PromotionProductResponse.class))
                            .collect(Collectors.toList()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromotionResponse addPromotionProducts(Long promotionId, List<PromotionProductRequest> products) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));

        List<PromotionProduct> newProducts = products.stream().map(p -> {
            PromotionProduct pp = new PromotionProduct();
            pp.setPromotion(promotion);
            pp.setProductVariantId(p.getProductVariantId());
            pp.setDiscountType(p.getDiscountType());
            pp.setDiscountValue(p.getDiscountValue());
            return pp;
        }).collect(Collectors.toList());

        promotionProductRepository.saveAll(newProducts);

        // Đồng bộ giá nếu promotion đang active
        if (Boolean.TRUE.equals(promotion.getIsActive()) && isPromotionCurrentlyActive(promotion)) {
            syncVariantDiscounts(newProducts);
        }

        return getPromotion(promotionId);
    }

    @Override
    @Transactional
    public PromotionResponse removePromotionProducts(Long promotionId, List<Long> variantIds) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));

        // Reset giá trước khi xóa
        for (Long variantId : variantIds) {
            resetVariantDiscount(variantId);
        }

        promotionProductRepository.deleteByPromotionIdAndProductVariantIdIn(promotionId, variantIds);

        return getPromotion(promotionId);
    }

    @Override
    public CouponResponse validateCoupon(String couponCode, BigDecimal orderAmount) {
        CouponResponse response = new CouponResponse();
        response.setCode(couponCode);

        Optional<Coupon> couponOpt = couponRepository.findByCode(couponCode);
        if (couponOpt.isEmpty()) {
            response.setValid(false);
            response.setMessage("Mã giảm giá không tồn tại");
            return response;
        }

        Coupon coupon = couponOpt.get();
        mapper.map(coupon, response);

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            response.setValid(false);
            response.setMessage("Mã giảm giá đã bị vô hiệu hóa");
            return response;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            response.setValid(false);
            response.setMessage("Mã giảm giá đã hết hạn hoặc chưa đến ngày áp dụng");
            return response;
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
            response.setValid(false);
            response.setMessage("Mã giảm giá đã hết lượt sử dụng");
            return response;
        }

        response.setValid(true);
        response.setMessage("Mã giảm giá hợp lệ");

        // Tính số tiền giảm thực tế nếu có orderAmount
        if (orderAmount != null && orderAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (orderAmount.compareTo(coupon.getMinOrderValue()) >= 0) {
                BigDecimal discount = orderAmount.multiply(coupon.getDiscountRate())
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discount = coupon.getMaxDiscountAmount();
                }
                response.setActualDiscountAmount(discount);
            } else {
                response.setActualDiscountAmount(BigDecimal.ZERO);
                response.setMessage("Mã giảm giá hợp lệ, nhưng đơn hàng chưa đạt giá trị tối thiểu "
                        + coupon.getMinOrderValue().setScale(0, RoundingMode.HALF_UP) + " VNĐ");
            }
        }

        return response;
    }

    // ==================== Helper Methods ====================

    /**
     * Đồng bộ discountPrice/discountRate xuống ProductVariant khi promotion active.
     */
    private void syncVariantDiscounts(List<PromotionProduct> promotionProducts) {
        for (PromotionProduct pp : promotionProducts) {
            ProductVariant variant = productVariantRepository.findById(pp.getProductVariantId()).orElse(null);
            if (variant != null) {
                applyDiscountToVariant(variant, pp.getDiscountType(), pp.getDiscountValue());
                productVariantRepository.save(variant);
            }
        }
    }

    /**
     * Áp dụng discount lên variant theo loại (PERCENTAGE hoặc FIXED).
     */
    private void applyDiscountToVariant(ProductVariant variant, DiscountType discountType, BigDecimal discountValue) {
        if (discountType == DiscountType.PERCENTAGE) {
            BigDecimal rate = discountValue;
            BigDecimal discountAmount = variant.getPrice().multiply(rate)
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            variant.setDiscountPrice(variant.getPrice().subtract(discountAmount));
            variant.setDiscountRate(rate);
        } else if (discountType == DiscountType.FIXED) {
            BigDecimal discountPrice = variant.getPrice().subtract(discountValue);
            if (discountPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountPrice = BigDecimal.ZERO;
            }
            variant.setDiscountPrice(discountPrice);

            // Tính rate từ fixed amount
            if (variant.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = discountValue.multiply(new BigDecimal("100"))
                        .divide(variant.getPrice(), 2, RoundingMode.HALF_UP);
                variant.setDiscountRate(rate);
            }
        }
    }

    /**
     * Reset discountPrice/discountRate cho tất cả variant trong 1 promotion.
     */
    private void resetVariantDiscountsForPromotion(Long promotionId) {
        List<PromotionProduct> products = promotionProductRepository.findByPromotionId(promotionId);
        for (PromotionProduct pp : products) {
            resetVariantDiscount(pp.getProductVariantId());
        }
    }

    /**
     * Reset discountPrice/discountRate cho 1 variant cụ thể.
     * Kiểm tra xem variant có thuộc promotion active nào khác không.
     */
    private void resetVariantDiscount(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
        if (variant != null) {
            variant.setDiscountPrice(null);
            variant.setDiscountRate(null);
            productVariantRepository.save(variant);
        }
    }

    private boolean isPromotionCurrentlyActive(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(promotion.getStartDate()) && now.isBefore(promotion.getEndDate());
    }
}
