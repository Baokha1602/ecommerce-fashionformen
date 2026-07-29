package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import com.example.ecommerce_fashionformen.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminUserController – Quản lý người dùng (chỉ dành cho ADMIN).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/users                  – Danh sách người dùng (phân trang + lọc)</li>
 *   <li>GET  /api/users/{id}             – Chi tiết một người dùng</li>
 *   <li>PATCH /api/users/{id}/status     – Khoá / mở khoá tài khoản</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /**
     * GET /api/users
     * <p>Trả về danh sách người dùng với phân trang và bộ lọc tuỳ chọn.
     *
     * @param keyword  Tìm kiếm theo fullName / email / phone / username (không bắt buộc)
     * @param role     Lọc theo vai trò: USER | ADMIN (không bắt buộc)
     * @param isActive Lọc theo trạng thái: true = đang hoạt động, false = bị khoá (không bắt buộc)
     * @param pageable Phân trang & sắp xếp (mặc định: page=0, size=20, sort=createdAt DESC)
     * @return Page<UserResponse>
     */
    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ApiResponse.success(
                "Lấy danh sách người dùng thành công",
                userService.getUsers(keyword, role, isActive, pageable));
    }

    /**
     * GET /api/users/{id}
     * <p>Trả về chi tiết một người dùng theo ID.
     *
     * @param id ID người dùng
     * @return UserResponse
     */
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(
                "Lấy thông tin người dùng thành công",
                userService.getUserById(id));
    }

    /**
     * PATCH /api/users/{id}/status
     * <p>Khoá hoặc mở khoá tài khoản người dùng.
     *
     * @param id       ID người dùng
     * @param isActive true = mở khoá, false = khoá tài khoản
     * @return UserResponse sau khi cập nhật
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {

        return ApiResponse.success(
                isActive ? "Mở khoá tài khoản thành công" : "Khoá tài khoản thành công",
                userService.updateUserStatus(id, isActive));
    }

    /**
     * DELETE /api/users/{id}
     * <p>Xóa mềm người dùng. Tài khoản sẽ được ẩn khỏi hệ thống nhưng không bị xóa khỏi cơ sở dữ liệu.
     *
     * @param id ID người dùng
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.successMessage("Xóa người dùng thành công");
    }
}
