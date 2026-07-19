package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** Tạo đơn hàng mới (phí ship tính từ GHN) */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestParam Long userId,
            @RequestBody @Valid OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    /** Lịch sử đơn hàng của user */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    /** Chi tiết một đơn hàng */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    /**
     * [ADMIN] Giao hàng: tạo đơn GHN, lưu shipment,
     * chuyển trạng thái đơn sang DELIVERING.
     * Yêu cầu đơn hàng phải ở trạng thái PROCESSING.
     */
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.deliverOrder(orderId));
    }

    /**
     * [ADMIN] Cập nhật trạng thái đơn hàng thủ công.
     * VD: PENDING → PROCESSING, DELIVERING → DELIVERED, → CANCELLED
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}
