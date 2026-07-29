package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantsRepository extends JpaRepository<ProductVariant, Long> {

    // Pessimistic lock khi trừ/cộng tồn kho (tránh race condition mua cùng sản phẩm)
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id = :id")
    Optional<ProductVariant> findByIdForUpdate(@Param("id") Long id);

    List<ProductVariant> findByIdIn(List<Long> ids);

    // Soft-delete aware queries
    Optional<ProductVariant> findByIdAndIsDeletedFalse(Long id);
    List<ProductVariant> findAllByIsDeletedFalse();
}
