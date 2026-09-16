package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.dto.order.OrderAdminResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderStatsResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderStatusUpdateRequest;
import com.example.ecommerce_fashionformen.services.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ApiResponse.success("Lấy danh sách đơn hàng thành công",
                adminOrderService.getAllOrders(orderStatus, paymentMethod, dateFrom, dateTo, pageable));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderAdminResponse> getOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.success("Lấy chi tiết đơn hàng thành công",
                adminOrderService.getOrderDetail(orderId));
    }

    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                         @RequestBody @Valid OrderStatusUpdateRequest request) {
        String adminUsername = getCurrentUsername();
        return ApiResponse.success("Cập nhật trạng thái đơn hàng thành công",
                adminOrderService.updateOrderStatus(orderId, request.getNewStatus(),
                        request.getNote(), adminUsername));
    }

    @GetMapping("/stats")
    public ApiResponse<OrderStatsResponse> getOrderStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return ApiResponse.success("Lấy thống kê đơn hàng thành công",
                adminOrderService.getOrderStats(dateFrom, dateTo));
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }
}
