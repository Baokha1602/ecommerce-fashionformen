package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    /**
     * Pessimistic Write Lock: khóa dòng Order trong DB khi xử lý IPN/Return đồng thời.
     * Chống race condition khi MoMo gọi IPN nhiều lần hoặc IPN + Redirect chạy song song.
     * Yêu cầu: phải được gọi trong một @Transactional context.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    java.util.Optional<Order> findByIdWithLock(@Param("id") Long id);

    // Tìm đơn hàng cần tự động hủy (thanh toán online quá hạn)
    List<Order> findByOrderStatusAndPaymentMethodInAndIsPaidFalseAndCreatedAtBefore(
            OrderStatus orderStatus,
            List<PaymentMethod> paymentMethods,
            LocalDateTime cutoffTime
    );

    // Thống kê tổng doanh thu theo khoảng thời gian (chỉ đơn DELIVERED)
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.orderStatus = 'DELIVERED' " +
            "AND o.createdAt BETWEEN :from AND :to")
    BigDecimal sumRevenueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Đếm đơn theo trạng thái trong khoảng thời gian
    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to GROUP BY o.orderStatus")
    List<Object[]> countByStatusAndDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
