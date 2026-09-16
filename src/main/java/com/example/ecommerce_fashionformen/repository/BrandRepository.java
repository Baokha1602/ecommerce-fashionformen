package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
    List<Brand> findByIsActive(Boolean isActive);

    // Soft-delete aware queries
    List<Brand> findAllByIsDeletedFalse();
    Optional<Brand> findByIdAndIsDeletedFalse(Long id);
    List<Brand> findByIsActiveAndIsDeletedFalse(Boolean isActive);
}
