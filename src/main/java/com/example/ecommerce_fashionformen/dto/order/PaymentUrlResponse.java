package com.example.ecommerce_fashionformen.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUrlResponse {
    private Long orderId;
    private String paymentUrl;
}
