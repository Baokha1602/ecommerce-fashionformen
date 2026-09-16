package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.brand.BrandResponse;
import com.example.ecommerce_fashionformen.dto.brand.BrandUpsertRequest;

import java.util.List;

public interface BrandService {

    List<BrandResponse> findAll();

    List<BrandResponse> findAllActive();

    BrandResponse findById(Long id);

    BrandResponse create(BrandUpsertRequest request);

    BrandResponse update(Long id, BrandUpsertRequest request);

    void delete(Long id);
}
