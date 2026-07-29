package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);

    // Soft-delete aware queries
    List<Banner> findAllByIsDeletedFalse();
    Optional<Banner> findByIdAndIsDeletedFalse(Long id);
    List<Banner> findByIsActiveAndIsDeletedFalseOrderByDisplayOrderAsc(Boolean isActive);
}
