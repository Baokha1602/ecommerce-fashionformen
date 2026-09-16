package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Promotions đang chạy (active + trong khoảng ngày + chưa xóa mềm)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now AND p.isDeleted = false")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    // Promotions hết hạn cần deactivate (active nhưng quá endDate + chưa xóa mềm)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.endDate < :now AND p.isDeleted = false")
    List<Promotion> findExpiredActivePromotions(@Param("now") LocalDateTime now);

    // Promotions chưa active nhưng đã đến startDate (cần kích hoạt + chưa xóa mềm)
    @Query("SELECT p FROM Promotion p WHERE p.isActive = false AND p.startDate <= :now AND p.endDate >= :now AND p.isDeleted = false")
    List<Promotion> findPendingPromotions(@Param("now") LocalDateTime now);

    // Soft-delete aware queries
    List<Promotion> findAllByIsDeletedFalse();
    Optional<Promotion> findByIdAndIsDeletedFalse(Long id);
}
