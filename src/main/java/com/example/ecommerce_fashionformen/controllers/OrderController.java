package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestParam Long userId, @RequestBody @Valid OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }
}
