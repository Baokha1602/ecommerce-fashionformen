package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.dto.order.OrderAdminResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AdminOrderService {
    Page<OrderResponse> getAllOrders(OrderStatus orderStatus, PaymentMethod paymentMethod,
                                     LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);
    OrderAdminResponse getOrderDetail(Long orderId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, String note, String adminUsername);
    OrderStatsResponse getOrderStats(LocalDateTime dateFrom, LocalDateTime dateTo);
}
