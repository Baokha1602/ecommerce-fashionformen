package com.example.ecommerce_fashionformen.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatsResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private Map<String, Long> orderCountByStatus;
}
