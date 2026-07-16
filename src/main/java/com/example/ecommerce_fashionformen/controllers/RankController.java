package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.rank.RankResponse;
import com.example.ecommerce_fashionformen.dto.rank.RankUpsertRequest;
import com.example.ecommerce_fashionformen.services.RankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranks")
public class RankController {

    private final RankService rankService;

    @GetMapping
    public ApiResponse<List<RankResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách hạng thành công", rankService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<RankResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy hạng thành công", rankService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RankResponse>> create(@Valid @RequestBody RankUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo hạng thành công", rankService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<RankResponse> update(@PathVariable Long id, @Valid @RequestBody RankUpsertRequest request) {
        return ApiResponse.success("Cập nhật hạng thành công", rankService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<RankResponse> patch(@PathVariable Long id, @RequestBody RankUpsertRequest request) {
        return ApiResponse.success("Cập nhật hạng thành công", rankService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        rankService.delete(id);
        return ApiResponse.successMessage("Xóa hạng thành công");
    }
}
