package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import com.example.ecommerce_fashionformen.domain.enums.RankName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Table(name = "ranks")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
public class Rank extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_name", nullable = false, unique = true, length = 20)
    private RankName rankName;

    @Column(name = "point", nullable = false)
    private int point = 0;

    @Column(name = "rank_discount", nullable = false)
    private double rankDiscount = 0.0;
}
