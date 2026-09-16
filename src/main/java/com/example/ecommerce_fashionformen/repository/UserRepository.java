package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 1. Tìm kiếm phục vụ Đăng nhập & Xác thực
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);


    // 2. Kiểm tra trùng lặp dữ liệu khi Đăng ký/Cập nhật
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // 3. Phục vụ Quản lý & Phân trang phía Admin
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    List<User> findByUserRole(UserRole userRole);

    // 4. Lấy danh sách user theo nhiều role — dùng cho Notification System
    List<User> findByUserRoleIn(List<UserRole> roles);

    // 5. Soft-delete aware queries
    Optional<User> findByIdAndIsDeletedFalse(Long id);

}
