package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    // Soft-delete aware queries
    List<Rank> findAllByIsDeletedFalse();
    Optional<Rank> findByIdAndIsDeletedFalse(Long id);
}

