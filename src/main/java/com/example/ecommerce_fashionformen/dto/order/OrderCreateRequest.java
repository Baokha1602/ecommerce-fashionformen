package com.example.ecommerce_fashionformen.dto.order;

import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "ID giỏ hàng không được để trống")
    private Long cartId;

    @NotNull(message = "Địa chỉ người dùng không được để trống")
    private Long userAddressId;

    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    private String email;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;

    private String notes;

    @Valid
    private List<OrderItemRequest> orderItems;

    private String couponCode;
}
