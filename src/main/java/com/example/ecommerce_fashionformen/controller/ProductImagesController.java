package com.example.ecommerce_fashionformen.controller;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.ProductImagesRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductImagesResponse;
import com.example.ecommerce_fashionformen.services.ProductImagesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImagesController {

    private final ProductImagesService productImagesService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductImagesResponse>> createProductImages(@Valid @RequestBody ProductImagesRequest request) {
        ProductImagesResponse response = productImagesService.createProductImages(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("ProductImages created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductImagesResponse>> getProductImagesById(@PathVariable Long id) {
        ProductImagesResponse response = productImagesService.getProductImagesById(id);
        return ResponseEntity.ok(ApiResponse.success("ProductImages retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductImagesResponse>>> getAllProductImagess() {
        return ResponseEntity.ok(ApiResponse.success("ProductImagess retrieved successfully", productImagesService.getAllProductImagess()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductImagesResponse>> updateProductImages(
            @PathVariable Long id,
            @Valid @RequestBody ProductImagesRequest request) {
        ProductImagesResponse response = productImagesService.updateProductImages(id, request);
        return ResponseEntity.ok(ApiResponse.success("ProductImages updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImages(@PathVariable Long id) {
        productImagesService.deleteProductImages(id);
        return ResponseEntity.ok(ApiResponse.successMessage("ProductImages deleted successfully"));
    }
}
