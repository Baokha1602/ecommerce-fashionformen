package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /** Tìm vận đơn theo ID đơn hàng */
    Optional<Shipment> findByOrderId(Long orderId);

    /** Tìm vận đơn theo mã GHN */
    Optional<Shipment> findByGhnOrderCode(String ghnOrderCode);
}
