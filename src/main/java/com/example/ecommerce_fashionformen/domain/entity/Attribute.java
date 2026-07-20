package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attributes")
@Getter
@Setter
public class Attribute extends AuditableEntity {
    @Column(name = "name", nullable = false)
    private String name;
}
