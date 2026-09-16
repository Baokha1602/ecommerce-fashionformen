package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.ProductTagRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductTagResponse;
import com.example.ecommerce_fashionformen.services.ProductTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-tags")
@RequiredArgsConstructor
public class ProductTagController {

    private final ProductTagService productTagService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductTagResponse>> createProductTag(@Valid @RequestBody ProductTagRequest request) {
        ProductTagResponse response = productTagService.createProductTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("ProductTag created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTagResponse>> getProductTagById(@PathVariable Long id) {
        ProductTagResponse response = productTagService.getProductTagById(id);
        return ResponseEntity.ok(ApiResponse.success("ProductTag retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTagResponse>>> getAllProductTags() {
        return ResponseEntity.ok(ApiResponse.success("ProductTags retrieved successfully", productTagService.getAllProductTags()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTagResponse>> updateProductTag(
            @PathVariable Long id,
            @Valid @RequestBody ProductTagRequest request) {
        ProductTagResponse response = productTagService.updateProductTag(id, request);
        return ResponseEntity.ok(ApiResponse.success("ProductTag updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductTag(@PathVariable Long id) {
        productTagService.deleteProductTag(id);
        return ResponseEntity.ok(ApiResponse.successMessage("ProductTag deleted successfully"));
    }
}
