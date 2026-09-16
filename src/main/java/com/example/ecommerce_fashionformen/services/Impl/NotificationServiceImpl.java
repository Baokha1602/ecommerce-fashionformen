package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Notification;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.enums.NotificationType;
import com.example.ecommerce_fashionformen.domain.enums.UserRole;
import com.example.ecommerce_fashionformen.dto.notification.NotificationResponse;
import com.example.ecommerce_fashionformen.dto.notification.UnreadCountResponse;
import com.example.ecommerce_fashionformen.repository.NotificationRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    // ── REST API ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Long recipientId, Pageable pageable) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(n -> mapper.map(n, NotificationResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long recipientId) {
        long count = notificationRepository.countByRecipientIdAndIsRead(recipientId, false);
        return new UnreadCountResponse(count);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long recipientId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Thông báo không tồn tại"));

        // Kiểm tra ownership: chỉ recipient mới được đánh dấu
        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new BadRequestException("Bạn không có quyền thao tác trên thông báo này");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return mapper.map(notification, NotificationResponse.class);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long recipientId) {
        notificationRepository.markAllReadByRecipientId(recipientId);
    }

    // ── Triggers ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void notifyOrderPlaced(Long orderId, String customerName) {
        String title = "Đơn hàng mới #" + orderId;
        String body  = "Khách hàng " + customerName
                       + " vừa đặt đơn hàng mới. Vui lòng kiểm tra và xử lý.";
        createForAllAdminAndStaff(NotificationType.ORDER_PLACED, title, body, orderId);
        log.info("[NOTIFICATION] ORDER_PLACED - Đơn #{} bởi {}", orderId, customerName);
    }

    @Override
    @Transactional
    public void notifyOrderCancelled(Long orderId, String cancelledBy) {
        String title = "Đơn hàng #" + orderId + " bị hủy";
        String body  = "Đơn hàng #" + orderId + " đã bị hủy bởi " + cancelledBy + ".";
        createForAllAdminAndStaff(NotificationType.ORDER_CANCELLED, title, body, orderId);
        log.info("[NOTIFICATION] ORDER_CANCELLED - Đơn #{} bởi {}", orderId, cancelledBy);
    }

    @Override
    @Transactional
    public void notifyOrderPaymentSuccess(Long orderId, String paymentMethod) {
        String title = "Thanh toán thành công - Đơn #" + orderId;
        String body  = "Đơn hàng #" + orderId + " đã được thanh toán qua " + paymentMethod
                       + ". Vui lòng xử lý giao hàng.";
        createForAllAdminAndStaff(NotificationType.ORDER_PAYMENT_SUCCESS, title, body, orderId);
        log.info("[NOTIFICATION] ORDER_PAYMENT_SUCCESS - Đơn #{} qua {}", orderId, paymentMethod);
    }

    @Override
    @Transactional
    public void notifyOrderExpired(Long orderId) {
        String title = "Đơn hàng #" + orderId + " hết hạn thanh toán";
        String body  = "Đơn hàng #" + orderId
                       + " đã tự động bị hủy do quá thời gian thanh toán online.";
        createForAllAdminAndStaff(NotificationType.ORDER_EXPIRED, title, body, orderId);
        log.info("[NOTIFICATION] ORDER_EXPIRED - Đơn #{}", orderId);
    }

    // ── Helper ──────────────────────────────────────────────────────────────

    /**
     * Tạo notification cho tất cả User có role ADMIN hoặc STAFF.
     * Dùng saveAll() để batch insert, tránh N+1.
     */
    private void createForAllAdminAndStaff(NotificationType type, String title,
                                            String body, Long referenceId) {
        List<User> recipients = userRepository
                .findByUserRoleIn(List.of(UserRole.ADMIN, UserRole.STAFF));

        if (recipients.isEmpty()) {
            log.warn("[NOTIFICATION] Không tìm thấy ADMIN/STAFF nào để gửi thông báo. type={}", type);
            return;
        }

        List<Notification> notifications = recipients.stream()
                .map(user -> Notification.builder()
                        .recipient(user)
                        .type(type)
                        .title(title)
                        .body(body)
                        .referenceId(referenceId)
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}
