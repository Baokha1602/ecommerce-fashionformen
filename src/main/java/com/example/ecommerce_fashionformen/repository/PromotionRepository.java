package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Promotions đang chạy (active + trong khoảng ngày)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    // Promotions hết hạn cần deactivate (active nhưng quá endDate)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.endDate < :now")
    List<Promotion> findExpiredActivePromotions(@Param("now") LocalDateTime now);

    // Promotions chưa active nhưng đã đến startDate (cần kích hoạt)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = false AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findPendingPromotions(@Param("now") LocalDateTime now);
}
