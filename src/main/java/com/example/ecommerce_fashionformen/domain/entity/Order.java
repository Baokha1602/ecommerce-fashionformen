package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import com.example.ecommerce_fashionformen.domain.BaseEntity;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "orders")

public class Order extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_address_id")
    private Long userAddressId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "subtotal_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalOriginal = BigDecimal.ZERO;

    @Column(name = "product_discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal productDiscountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingFeeOriginal = BigDecimal.ZERO;

    @Column(name = "shipping_fee_actual", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingFeeActual = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_order_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalOrderAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "rank_discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal rankDiscountAmount = BigDecimal.ZERO;

    @Column(name = "coupon_discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal couponDiscountAmount = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

}
