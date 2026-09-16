package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.tag.TagCreateRequest;
import com.example.ecommerce_fashionformen.dto.tag.TagResponse;
import com.example.ecommerce_fashionformen.dto.tag.TagUpdateRequest;
import com.example.ecommerce_fashionformen.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ApiResponse<List<TagResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách thẻ tag thành công", tagService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<TagResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy thẻ tag thành công", tagService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thẻ tag thành công", tagService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<TagResponse> update(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request) {
        return ApiResponse.success("Cập nhật thẻ tag thành công", tagService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ApiResponse.successMessage("Xóa thẻ tag thành công");
    }
}
