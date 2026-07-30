package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import com.example.ecommerce_fashionformen.dto.user.UserUpdateRequest;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// API endpoints quan ly va cap nhat thong tin nguoi dung
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ApiResponse.success(
                "Lấy danh sách người dùng thành công",
                userService.getUsers(keyword, role, isActive, pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.success(
                "Lấy thông tin cá nhân thành công",
                userService.getUserById(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(
                "Lấy thông tin người dùng thành công",
                userService.getUserById(id));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> updateMyProfile(@Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(
                "Cập nhật thông tin cá nhân thành công",
                userService.updateUser(getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(
                "Cập nhật thông tin người dùng thành công",
                userService.updateUser(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> patchUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(
                "Cập nhật thông tin người dùng thành công",
                userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {

        return ApiResponse.success(
                isActive ? "Mở khoá tài khoản thành công" : "Khoá tài khoản thành công",
                userService.updateUserStatus(id, isActive));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.successMessage("Xóa người dùng thành công");
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng hiện tại"));
        return user.getId();
    }
}
