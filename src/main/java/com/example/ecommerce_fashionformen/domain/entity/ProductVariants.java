package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "product_variants")
@Entity
@Setter
@Getter
public class ProductVariants extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "stock_total")
    private int stockTotal = 0;

    @Column(name = "stock_lock")
    private int stockLock = 0;

}
