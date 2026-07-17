package com.example.ecommerce_fashionformen.controller;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.TagRequest;
import com.example.ecommerce_fashionformen.dto.response.TagResponse;
import com.example.ecommerce_fashionformen.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@Valid @RequestBody TagRequest request) {
        TagResponse response = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tag created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> getTagById(@PathVariable Long id) {
        TagResponse response = tagService.getTagById(id);
        return ResponseEntity.ok(ApiResponse.success("Tag retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        return ResponseEntity.ok(ApiResponse.success("Tags retrieved successfully", tagService.getAllTags()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request) {
        TagResponse response = tagService.updateTag(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tag updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Tag deleted successfully"));
    }
}
