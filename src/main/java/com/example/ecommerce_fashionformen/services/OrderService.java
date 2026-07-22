package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderCreateRequest request);
    Page<OrderResponse> getOrderHistory(Long userId, Pageable pageable);
    OrderResponse getOrderDetails(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);
}
