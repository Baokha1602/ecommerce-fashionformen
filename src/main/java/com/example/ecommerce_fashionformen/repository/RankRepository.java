package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
}
