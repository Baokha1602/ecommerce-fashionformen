package com.example.ecommerce_fashionformen.dto.order;

import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {

    @NotNull(message = "Trạng thái mới không được để trống")
    private OrderStatus newStatus;

    private String note;
}
