package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import com.example.ecommerce_fashionformen.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Table(name = "product_images")
@Entity
@Setter
@Getter
public class ProductImages extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "url_image", nullable = false)
    private String image;

    @Column(name = "is_main_image")
    private boolean isMainImage = false;


}
