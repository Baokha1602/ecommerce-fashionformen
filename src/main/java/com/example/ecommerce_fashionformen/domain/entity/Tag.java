package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tags")
@Entity
@Setter
@Getter
public class Tag extends AuditableEntity {

    @Column(nullable = false,unique = true)
    private String name;

    private String description;
}
