package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Tìm kiếm phục vụ Đăng nhập & Xác thực
    Optional<User> findByEmail(String email);


    // 2. Kiểm tra trùng lặp dữ liệu khi Đăng ký/Cập nhật
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // 3. Phục vụ Quản lý & Phân trang phía Admin
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    List<User> findByUserRole(UserRole userRole);

}