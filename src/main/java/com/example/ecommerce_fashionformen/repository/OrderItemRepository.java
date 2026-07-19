package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** Lấy tất cả OrderItem theo ID đơn hàng (qua quan hệ Order.id) */
    List<OrderItem> findByOrder_Id(Long orderId);
}
