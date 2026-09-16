package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.ProductReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewsRepository extends JpaRepository<ProductReviews, Long> {

    // Soft-delete aware queries
    Optional<ProductReviews> findByIdAndIsDeletedFalse(Long id);
    List<ProductReviews> findAllByIsDeletedFalse();
}

