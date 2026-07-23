package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.notification.NotificationResponse;
import com.example.ecommerce_fashionformen.dto.notification.UnreadCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    // ── REST API ────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách thông báo của người dùng hiện tại, phân trang, mới nhất trước.
     */
    Page<NotificationResponse> getMyNotifications(Long recipientId, Pageable pageable);

    /**
     * Lấy số lượng thông báo chưa đọc của người dùng hiện tại.
     */
    UnreadCountResponse getUnreadCount(Long recipientId);

    /**
     * Đánh dấu 1 thông báo cụ thể là đã đọc.
     * Kiểm tra ownership: chỉ recipient mới được đánh dấu.
     */
    NotificationResponse markAsRead(Long recipientId, Long notificationId);

    /**
     * Đánh dấu tất cả thông báo chưa đọc của người dùng là đã đọc.
     */
    void markAllAsRead(Long recipientId);

    // ── Triggers — gọi nội bộ từ các service xử lý đơn hàng ───────────────

    /**
     * Tạo thông báo ORDER_PLACED cho tất cả ADMIN và STAFF.
     * Hook: OrderServiceImpl.createOrder()
     */
    void notifyOrderPlaced(Long orderId, String customerName);

    /**
     * Tạo thông báo ORDER_CANCELLED cho tất cả ADMIN và STAFF.
     * Hook: OrderServiceImpl.cancelOrder() và AdminOrderServiceImpl.updateOrderStatus(CANCELLED)
     */
    void notifyOrderCancelled(Long orderId, String cancelledBy);

    /**
     * Tạo thông báo ORDER_PAYMENT_SUCCESS cho tất cả ADMIN và STAFF.
     * Hook: PaymentServiceImpl.updateOrderPaymentSuccess()
     */
    void notifyOrderPaymentSuccess(Long orderId, String paymentMethod);

    /**
     * Tạo thông báo ORDER_EXPIRED cho tất cả ADMIN và STAFF.
     * Hook: OrderAutoExpireSchedulerService.autoExpireUnpaidOrders()
     */
    void notifyOrderExpired(Long orderId);
}
