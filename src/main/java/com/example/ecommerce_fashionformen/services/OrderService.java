package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderCreateRequest request);
    List<OrderResponse> getOrderHistory(Long userId);
    OrderResponse getOrderDetails(Long orderId);
}
