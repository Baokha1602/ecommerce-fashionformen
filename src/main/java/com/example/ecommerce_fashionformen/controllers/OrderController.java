package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestParam Long userId,
            @RequestBody @Valid OrderCreateRequest request) {
        OrderResponse orderResponse = orderService.createOrder(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo đơn hàng thành công", orderResponse));
    }

    /** Lịch sử đơn hàng của user */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(@RequestParam Long userId) {
        List<OrderResponse> history = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử đơn hàng thành công", history));
    }

    /** Chi tiết một đơn hàng */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(@PathVariable Long orderId) {
        OrderResponse orderDetails = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đơn hàng thành công", orderDetails));
    }

    /**
     * [ADMIN] Giao hàng: tạo đơn GHN, lưu shipment,
     * chuyển trạng thái đơn sang DELIVERING.
     * Yêu cầu đơn hàng phải ở trạng thái PROCESSING.
     */
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<ApiResponse<OrderResponse>> deliverOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.deliverOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Kích hoạt giao hàng qua GHN thành công", orderResponse));
    }

    /**
     * [ADMIN] Cập nhật trạng thái đơn hàng thủ công.
     * VD: PENDING → PROCESSING, DELIVERING → DELIVERED, → CANCELLED
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng thành công", orderResponse));
    }
}

