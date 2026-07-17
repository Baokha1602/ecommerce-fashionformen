package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.BrandRequest;
import com.example.ecommerce_fashionformen.dto.response.BrandResponse;
import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse getBrandById(Long id);
    List<BrandResponse> getAllBrands();
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
}
