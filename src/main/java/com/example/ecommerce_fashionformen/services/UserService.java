package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /**
     * Lấy danh sách người dùng với phân trang và bộ lọc.
     *
     * @param keyword  Từ khóa tìm kiếm theo fullName, email, phone, username (nullable)
     * @param role     Lọc theo vai trò người dùng (nullable)
     * @param isActive Lọc theo trạng thái hoạt động (nullable)
     * @param pageable Thông tin phân trang và sắp xếp
     * @return Page chứa danh sách UserResponse
     */
    Page<UserResponse> getUsers(String keyword, UserRole role, Boolean isActive, Pageable pageable);

    /**
     * Lấy thông tin chi tiết một người dùng theo ID.
     *
     * @param id ID của người dùng
     * @return UserResponse
     */
    UserResponse getUserById(Long id);

    /**
     * Cập nhật trạng thái hoạt động của người dùng (khoá / mở khoá tài khoản).
     *
     * @param id       ID của người dùng
     * @param isActive Trạng thái mới
     * @return UserResponse sau khi cập nhật
     */
    UserResponse updateUserStatus(Long id, Boolean isActive);

    /**
     * Xóa mềm người dùng (soft delete). Không xóa khỏi cơ sở dữ liệu.
     *
     * @param id ID của người dùng
     */
    void deleteUser(Long id);
}
