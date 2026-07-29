package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.user.UserResponse;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ─── Mapping ────────────────────────────────────────────────────────────────

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setFullName(user.getFullName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCurrentPoint(user.getCurrentPoint());
        response.setUserRole(user.getUserRole());
        response.setIsActive(user.getIsActive());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        if (user.getRank() != null && user.getRank().getRankName() != null) {
            response.setRankName(user.getRank().getRankName().name());
        }
        return response;
    }

    // ─── Specifications (dynamic query builder) ──────────────────────────────────

    private Specification<User> buildSpec(String keyword, UserRole role, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc bỏ user đã bị xóa mềm
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("phone")), like),
                        cb.like(cb.lower(root.get("username")), like)
                ));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("userRole"), role));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ─── Service methods ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(String keyword, UserRole role, Boolean isActive, Pageable pageable) {
        return userRepository.findAll(buildSpec(keyword, role, isActive), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + id));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, Boolean isActive) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setIsActive(isActive);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.softDelete();
        // Đồng thời vô hiệu hóa tài khoản
        user.setIsActive(false);
        userRepository.save(user);
    }
}
