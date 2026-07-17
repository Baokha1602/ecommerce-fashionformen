package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Brand;
import com.example.ecommerce_fashionformen.dto.request.BrandRequest;
import com.example.ecommerce_fashionformen.dto.response.BrandResponse;
import com.example.ecommerce_fashionformen.repository.BrandRepository;
import com.example.ecommerce_fashionformen.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        Brand entity = new Brand();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setLogoUrl(request.getLogoUrl());
        if(request.getIsActive() != null) entity.setIsActive(request.getIsActive());
        return mapToResponse(brandRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setLogoUrl(request.getLogoUrl());
        if(request.getIsActive() != null) entity.setIsActive(request.getIsActive());
        return mapToResponse(brandRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));
        brandRepository.delete(entity);
    }

    private BrandResponse mapToResponse(Brand entity) {
        BrandResponse response = new BrandResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setLogoUrl(entity.getLogoUrl());
        response.setIsActive(entity.getIsActive());
        return response;
    }
}
