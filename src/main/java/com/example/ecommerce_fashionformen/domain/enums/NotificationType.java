package com.example.ecommerce_fashionformen.domain.enums;

public enum NotificationType {
    ORDER_PLACED,           // Khách hàng đặt đơn mới
    ORDER_CANCELLED,        // Đơn hàng bị hủy (bởi khách hoặc admin/staff)
    ORDER_PAYMENT_SUCCESS,  // Thanh toán online thành công (VNPay / MoMo)
    ORDER_EXPIRED           // Đơn hàng tự động hủy do quá thời gian thanh toán
}
