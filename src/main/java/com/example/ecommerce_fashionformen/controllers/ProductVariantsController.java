package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.ProductVariantsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantsResponse;
import com.example.ecommerce_fashionformen.services.ProductVariantsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
public class ProductVariantsController {

    private final ProductVariantsService productVariantsService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductVariantsResponse>> createProductVariants(@Valid @RequestBody ProductVariantsRequest request) {
        ProductVariantsResponse response = productVariantsService.createProductVariants(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("ProductVariants created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantsResponse>> getProductVariantsById(@PathVariable Long id) {
        ProductVariantsResponse response = productVariantsService.getProductVariantsById(id);
        return ResponseEntity.ok(ApiResponse.success("ProductVariants retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductVariantsResponse>>> getAllProductVariantss() {
        return ResponseEntity.ok(ApiResponse.success("ProductVariantss retrieved successfully", productVariantsService.getAllProductVariantss()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantsResponse>> updateProductVariants(
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantsRequest request) {
        ProductVariantsResponse response = productVariantsService.updateProductVariants(id, request);
        return ResponseEntity.ok(ApiResponse.success("ProductVariants updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductVariants(@PathVariable Long id) {
        productVariantsService.deleteProductVariants(id);
        return ResponseEntity.ok(ApiResponse.successMessage("ProductVariants deleted successfully"));
    }
}
