package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.ConflictException;
import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Brand;
import com.example.ecommerce_fashionformen.dto.brand.BrandResponse;
import com.example.ecommerce_fashionformen.dto.brand.BrandUpsertRequest;
import com.example.ecommerce_fashionformen.repository.BrandRepository;
import com.example.ecommerce_fashionformen.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper mapper;

    private BrandResponse mapToResponse(Brand brand) {
        return mapper.map(brand, BrandResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> findAllActive() {
        return brandRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thương hiệu với ID: " + id));
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse create(BrandUpsertRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên thương hiệu đã tồn tại: " + request.getName());
        }

        Brand brand = mapper.map(request, Brand.class);
        if (request.getIsActive() == null) {
            brand.setIsActive(true);
        }
        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse update(Long id, BrandUpsertRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        // Kiểm tra trùng tên với brand khác
        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên thương hiệu đã tồn tại: " + request.getName());
        }

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        if (request.getIsActive() != null) {
            brand.setIsActive(request.getIsActive());
        }

        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thương hiệu với ID: " + id));
        brandRepository.delete(brand);
    }
}
