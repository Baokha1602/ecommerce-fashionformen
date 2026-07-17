package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "ranks")
@Getter
@Setter
@Entity
public class Rank extends AuditableEntity {

    @Column(name = "rank_name", nullable = false, unique = true)
    private String rankName;

    private int point;

    @Column(name = "rank_discount", precision = 5, scale = 2)
    private BigDecimal rankDiscount;


}
