package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.dto.order.PaymentUrlResponse;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.OrderService;
import com.example.ecommerce_fashionformen.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        Long userId = getCurrentUserId();
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo đơn hàng thành công", response));
    }

    @GetMapping
    public ApiResponse<Page<OrderResponse>> getOrderHistory(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Lấy lịch sử đơn hàng thành công",
                orderService.getOrderHistory(userId, pageable));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Lấy chi tiết đơn hàng thành công",
                orderService.getOrderDetails(userId, orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Hủy đơn hàng thành công",
                orderService.cancelOrder(userId, orderId));
    }

    @PostMapping("/{orderId}/payment/vnpay-url")
    public ApiResponse<PaymentUrlResponse> createVnPayUrl(@PathVariable Long orderId,
                                                           HttpServletRequest request) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Tạo URL thanh toán VNPay thành công",
                paymentService.createVnPayUrl(orderId, userId, request));
    }

    @PostMapping("/{orderId}/payment/momo-url")
    public ApiResponse<PaymentUrlResponse> createMoMoUrl(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Tạo URL thanh toán MoMo thành công",
                paymentService.createMoMoUrl(orderId, userId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getId();
    }
}
