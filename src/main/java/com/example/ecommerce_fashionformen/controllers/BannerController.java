package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.banner.BannerResponse;
import com.example.ecommerce_fashionformen.dto.banner.BannerUpsertRequest;
import com.example.ecommerce_fashionformen.services.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banners")
public class    BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ApiResponse<List<BannerResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách banner thành công", bannerService.findAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<BannerResponse>> getAllActive() {
        return ApiResponse.success("Lấy danh sách banner đang hoạt động thành công", bannerService.findAllActive());
    }

    @GetMapping("/{id}")
    public ApiResponse<BannerResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy banner thành công", bannerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BannerResponse>> create(@Valid @RequestBody BannerUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo banner thành công", bannerService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<BannerResponse> update(@PathVariable Long id, @Valid @RequestBody BannerUpsertRequest request) {
        return ApiResponse.success("Cập nhật banner thành công", bannerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ApiResponse.successMessage("Xóa banner thành công");
    }
}
