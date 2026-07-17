package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductTagRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductTagResponse;
import java.util.List;

public interface ProductTagService {
    ProductTagResponse createProductTag(ProductTagRequest request);
    ProductTagResponse getProductTagById(Long id);
    List<ProductTagResponse> getAllProductTags();
    ProductTagResponse updateProductTag(Long id, ProductTagRequest request);
    void deleteProductTag(Long id);
}
