package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {

    // Soft-delete aware queries
    Optional<ProductImages> findByIdAndIsDeletedFalse(Long id);
    List<ProductImages> findAllByIsDeletedFalse();
}

