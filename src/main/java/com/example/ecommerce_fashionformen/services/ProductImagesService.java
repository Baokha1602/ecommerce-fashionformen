package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductImagesRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductImagesResponse;
import java.util.List;

public interface ProductImagesService {
    ProductImagesResponse createProductImages(ProductImagesRequest request);
    ProductImagesResponse getProductImagesById(Long id);
    List<ProductImagesResponse> getAllProductImagess();
    ProductImagesResponse updateProductImages(Long id, ProductImagesRequest request);
    void deleteProductImages(Long id);
}
