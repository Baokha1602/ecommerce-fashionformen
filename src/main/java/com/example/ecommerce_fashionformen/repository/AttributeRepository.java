package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
