package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.address.UserAddressCreateRequest;
import com.example.ecommerce_fashionformen.dto.address.UserAddressResponse;
import com.example.ecommerce_fashionformen.dto.address.UserAddressUpdateRequest;
import com.example.ecommerce_fashionformen.services.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserAddressResponse>> getByUserId(@PathVariable Long userId) {
        return ApiResponse.success("Lấy danh sách địa chỉ thành công", userAddressService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserAddressResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Lấy địa chỉ thành công", userAddressService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressResponse>> create(@Valid @RequestBody UserAddressCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo địa chỉ thành công", userAddressService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserAddressResponse> update(@PathVariable Long id, @Valid @RequestBody UserAddressUpdateRequest request) {
        return ApiResponse.success("Cập nhật địa chỉ thành công", userAddressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userAddressService.delete(id);
        return ApiResponse.successMessage("Xóa địa chỉ thành công");
    }
}
