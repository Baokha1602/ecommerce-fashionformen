package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.notification.NotificationResponse;
import com.example.ecommerce_fashionformen.dto.notification.UnreadCountResponse;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST API cho hệ thống thông báo — chỉ ADMIN và STAFF được truy cập.
 *
 * GET  /api/notifications              — Lấy danh sách thông báo (phân trang, mới nhất trước)
 * GET  /api/notifications/unread-count — Lấy số thông báo chưa đọc
 * PATCH /api/notifications/{id}/read   — Đánh dấu 1 thông báo đã đọc
 * PATCH /api/notifications/read-all    — Đánh dấu tất cả thông báo đã đọc
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.success(
                "Lấy danh sách thông báo thành công",
                notificationService.getMyNotifications(getCurrentUserId(), pageable));
    }

    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount() {
        return ApiResponse.success(
                "Lấy số thông báo chưa đọc thành công",
                notificationService.getUnreadCount(getCurrentUserId()));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ApiResponse.success(
                "Đánh dấu thông báo đã đọc thành công",
                notificationService.markAsRead(getCurrentUserId(), id));
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return ApiResponse.successMessage("Đánh dấu tất cả thông báo đã đọc thành công");
    }

    // ── Helper — giống pattern trong OrderController ─────────────────────────

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getId();
    }
}
