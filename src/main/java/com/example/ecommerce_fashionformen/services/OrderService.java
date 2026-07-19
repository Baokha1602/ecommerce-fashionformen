package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderCreateRequest request);
    List<OrderResponse> getOrderHistory(Long userId);
    OrderResponse getOrderDetails(Long orderId);

    /**
     * Admin kích hoạt giao hàng: tạo đơn GHN, lưu shipment,
     * và chuyển trạng thái đơn sang DELIVERING.
     */
    OrderResponse deliverOrder(Long orderId);

    /**
     * Admin cập nhật trạng thái đơn hàng thủ công.
     */
    OrderResponse updateStatus(Long orderId, OrderStatus status);
}

