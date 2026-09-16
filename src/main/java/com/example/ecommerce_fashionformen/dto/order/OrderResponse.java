package com.example.ecommerce_fashionformen.dto.order;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long userAddressId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private Boolean isPaid;
    private BigDecimal subtotalOriginal;
    private BigDecimal productDiscountAmount;
    private BigDecimal rankDiscountAmount;
    private BigDecimal couponDiscountAmount;
    private String couponCode;
    private BigDecimal shippingFeeOriginal;
    private BigDecimal shippingFeeActual;
    private BigDecimal taxAmount;
    private BigDecimal totalOrderAmount;
    private BigDecimal finalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> orderItems;
}
