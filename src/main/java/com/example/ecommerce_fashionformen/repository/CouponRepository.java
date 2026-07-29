package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);

    // Pessimistic lock khi trừ usageLimit (tránh race condition đồng thời dùng coupon)
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.code = :code")
    Optional<Coupon> findByCodeForUpdate(@Param("code") String code);

    // Danh sách coupon còn hiệu lực cho customer
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now AND c.usageLimit > 0 AND c.isDeleted = false")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    // Soft-delete aware queries
    List<Coupon> findAllByIsDeletedFalse();
    Optional<Coupon> findByIdAndIsDeletedFalse(Long id);
    Optional<Coupon> findByCodeAndIsDeletedFalse(String code);
}
