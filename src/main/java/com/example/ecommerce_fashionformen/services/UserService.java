package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import com.example.ecommerce_fashionformen.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Nghiep vu quan ly va cap nhat thong tin nguoi dung
public interface UserService {

    Page<UserResponse> getUsers(String keyword, UserRole role, Boolean isActive, Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse updateUserStatus(Long id, Boolean isActive);

    void deleteUser(Long id);
}
