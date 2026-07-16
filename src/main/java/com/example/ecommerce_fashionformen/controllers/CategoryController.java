package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.category.CategoryResponse;
import com.example.ecommerce_fashionformen.dto.category.CategoryUpsertRequest;
import com.example.ecommerce_fashionformen.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách danh mục thành công", categoryService.findAll());
    }

    @GetMapping("/roots")
    public ApiResponse<List<CategoryResponse>> getRoots() {
        return ApiResponse.success("Lấy danh sách danh mục gốc thành công", categoryService.findRootCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy danh mục thành công", categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo danh mục thành công", categoryService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryUpsertRequest request) {
        return ApiResponse.success("Cập nhật danh mục thành công", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.successMessage("Xóa danh mục thành công");
    }
}
