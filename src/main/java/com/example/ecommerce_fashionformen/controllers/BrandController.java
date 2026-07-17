package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.brand.BrandResponse;
import com.example.ecommerce_fashionformen.dto.brand.BrandUpsertRequest;
import com.example.ecommerce_fashionformen.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách thương hiệu thành công", brandService.findAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<BrandResponse>> getAllActive() {
        return ApiResponse.success("Lấy danh sách thương hiệu đang hoạt động thành công", brandService.findAllActive());
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy thương hiệu thành công", brandService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thương hiệu thành công", brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(@PathVariable Long id, @Valid @RequestBody BrandUpsertRequest request) {
        return ApiResponse.success("Cập nhật thương hiệu thành công", brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ApiResponse.successMessage("Xóa thương hiệu thành công");
    }
}
