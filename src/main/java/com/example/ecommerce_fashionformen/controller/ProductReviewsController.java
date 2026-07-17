package com.example.ecommerce_fashionformen.controller;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.ProductReviewsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductReviewsResponse;
import com.example.ecommerce_fashionformen.services.ProductReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsController {

    private final ProductReviewsService productReviewsService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductReviewsResponse>> createProductReviews(@Valid @RequestBody ProductReviewsRequest request) {
        ProductReviewsResponse response = productReviewsService.createProductReviews(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("ProductReviews created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReviewsResponse>> getProductReviewsById(@PathVariable Long id) {
        ProductReviewsResponse response = productReviewsService.getProductReviewsById(id);
        return ResponseEntity.ok(ApiResponse.success("ProductReviews retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductReviewsResponse>>> getAllProductReviewss() {
        return ResponseEntity.ok(ApiResponse.success("ProductReviewss retrieved successfully", productReviewsService.getAllProductReviewss()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReviewsResponse>> updateProductReviews(
            @PathVariable Long id,
            @Valid @RequestBody ProductReviewsRequest request) {
        ProductReviewsResponse response = productReviewsService.updateProductReviews(id, request);
        return ResponseEntity.ok(ApiResponse.success("ProductReviews updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductReviews(@PathVariable Long id) {
        productReviewsService.deleteProductReviews(id);
        return ResponseEntity.ok(ApiResponse.successMessage("ProductReviews deleted successfully"));
    }
}
