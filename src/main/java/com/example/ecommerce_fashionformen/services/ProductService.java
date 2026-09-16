package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
