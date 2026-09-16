package com.example.ecommerce_fashionformen.dto.order;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryResponse {
    private Long id;
    private OrderStatus status;
    private String changedBy;
    private LocalDateTime changedAt;
    private String note;
}
