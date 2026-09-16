package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.ProductVariantRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantResponse;
import com.example.ecommerce_fashionformen.services.ProductVariantService;
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

    private final ProductVariantService productVariantService;

    @GetMapping
    public ApiResponse<List<ProductVariantResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách biến thể sản phẩm thành công", productVariantService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductVariantResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy biến thể sản phẩm thành công", productVariantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductVariantResponse>> create(@Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo biến thể sản phẩm thành công", productVariantService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductVariantResponse> update(@PathVariable Long id, @Valid @RequestBody ProductVariantRequest request) {
        return ApiResponse.success("Cập nhật biến thể sản phẩm thành công", productVariantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productVariantService.delete(id);
        return ApiResponse.successMessage("Xóa biến thể sản phẩm thành công");
    }
}
