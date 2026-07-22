package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.CouponUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageHistoryRepository extends JpaRepository<CouponUsageHistory, Long> {
    List<CouponUsageHistory> findByCouponId(Long couponId);
    long countByCouponId(Long couponId);
}
