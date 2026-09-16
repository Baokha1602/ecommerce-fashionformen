package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.ProductReviewsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductReviewsResponse;
import java.util.List;

public interface ProductReviewsService {
    ProductReviewsResponse createProductReviews(ProductReviewsRequest request);
    ProductReviewsResponse getProductReviewsById(Long id);
    List<ProductReviewsResponse> getAllProductReviewss();
    ProductReviewsResponse updateProductReviews(Long id, ProductReviewsRequest request);
    void deleteProductReviews(Long id);
}
