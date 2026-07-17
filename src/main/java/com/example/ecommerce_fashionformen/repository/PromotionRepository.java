package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
