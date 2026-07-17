package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.ConflictException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.dto.coupon.CouponCreateRequest;
import com.example.ecommerce_fashionformen.dto.coupon.CouponResponse;
import com.example.ecommerce_fashionformen.dto.coupon.CouponUpdateRequest;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.services.CouponService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    private CouponResponse mapToResponse(Coupon coupon) {
        return mapper.map(coupon, CouponResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse findById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá với ID: " + id));
        return mapToResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse findByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá: " + code));
        return mapToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse create(CouponCreateRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã giảm giá đã tồn tại: " + request.getCode());
        }

        // Validate ngày kết thúc phải sau ngày bắt đầu
        if (request.getEndDate().isBefore(request.getStartDate()) || request.getEndDate().isEqual(request.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        Coupon coupon = mapper.map(request, Coupon.class);
        if (request.getIsActive() == null) {
            coupon.setIsActive(true);
        }
        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponResponse update(Long id, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá với ID: " + id));

        // Cập nhật các trường non-null (PATCH semantics)
        if (request.getName() != null) {
            coupon.setName(request.getName());
        }
        if (request.getDiscountRate() != null) {
            coupon.setDiscountRate(request.getDiscountRate());
        }
        if (request.getMaxDiscountAmount() != null) {
            coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (request.getMinOrderValue() != null) {
            coupon.setMinOrderValue(request.getMinOrderValue());
        }
        if (request.getStartDate() != null) {
            coupon.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            coupon.setEndDate(request.getEndDate());
        }
        if (request.getUsageLimit() != null) {
            coupon.setUsageLimit(request.getUsageLimit());
        }
        if (request.getIsActive() != null) {
            coupon.setIsActive(request.getIsActive());
        }

        // Validate ngày sau khi cập nhật
        if (coupon.getEndDate().isBefore(coupon.getStartDate()) || coupon.getEndDate().isEqual(coupon.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá với ID: " + id));
        couponRepository.delete(coupon);
    }
}
