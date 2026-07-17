package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductVariantsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantsResponse;
import java.util.List;

public interface ProductVariantsService {
    ProductVariantsResponse createProductVariants(ProductVariantsRequest request);
    ProductVariantsResponse getProductVariantsById(Long id);
    List<ProductVariantsResponse> getAllProductVariantss();
    ProductVariantsResponse updateProductVariants(Long id, ProductVariantsRequest request);
    void deleteProductVariants(Long id);
}
