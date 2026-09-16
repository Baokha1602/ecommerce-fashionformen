package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductVariantRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {

    List<ProductVariantResponse> findAll();

    ProductVariantResponse findById(Long id);

    ProductVariantResponse create(ProductVariantRequest request);

    ProductVariantResponse update(Long id, ProductVariantRequest request);

    void delete(Long id);
}
