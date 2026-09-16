package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {
    List<PromotionProduct> findByPromotionId(Long promotionId);
    void deleteByPromotionIdAndProductVariantIdIn(Long promotionId, List<Long> variantIds);
    List<PromotionProduct> findByProductVariantId(Long productVariantId);
    void deleteByPromotionId(Long promotionId);
}
