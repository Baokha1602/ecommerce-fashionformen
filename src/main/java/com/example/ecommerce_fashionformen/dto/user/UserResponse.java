package com.example.ecommerce_fashionformen.dto.user;

import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    // Ảnh đại diện dạng Base64
    private String avatarImage;
    private int currentPoint;
    private UserRole userRole;
    private Boolean isActive;
    private LocalDate dateOfBirth;

    private String rankName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
