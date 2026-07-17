package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import com.example.ecommerce_fashionformen.domain.entity.PromotionProduct;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionCreateRequest;
import com.example.ecommerce_fashionformen.dto.promotion.PromotionResponse;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.repository.PromotionProductRepository;
import com.example.ecommerce_fashionformen.repository.PromotionRepository;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.dto.promotion.CouponResponse;
import com.example.ecommerce_fashionformen.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        Promotion promotion = mapper.map(request, Promotion.class);
        promotion = promotionRepository.save(promotion);
        
        if (request.getPromotionProducts() != null) {
            Promotion finalPromotion = promotion;
            List<PromotionProduct> products = request.getPromotionProducts().stream().map(p -> {
                PromotionProduct pp = mapper.map(p, PromotionProduct.class);
                pp.setPromotion(finalPromotion);
                return pp;
            }).collect(Collectors.toList());
            promotionProductRepository.saveAll(products);
        }
        
        return mapper.map(promotion, PromotionResponse.class);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionCreateRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));
        mapper.map(request, promotion);
        promotion = promotionRepository.save(promotion);
        return mapper.map(promotion, PromotionResponse.class);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public PromotionResponse getPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khuyến mãi không tồn tại"));
        return mapper.map(promotion, PromotionResponse.class);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(p -> mapper.map(p, PromotionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse validateCoupon(String couponCode) {
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
        return response;
    }
}
