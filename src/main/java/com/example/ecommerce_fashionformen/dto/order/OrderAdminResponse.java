package com.example.ecommerce_fashionformen.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderAdminResponse extends OrderResponse {
    private List<OrderStatusHistoryResponse> statusHistory;
}
