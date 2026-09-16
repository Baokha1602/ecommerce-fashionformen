package com.example.ecommerce_fashionformen.repository;

import com.example.ecommerce_fashionformen.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy tất cả thông báo của 1 recipient, sort mới nhất trước.
     */
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    /**
     * Đếm số thông báo chưa đọc (isRead=false) của 1 recipient.
     */
    long countByRecipientIdAndIsRead(Long recipientId, boolean isRead);

    /**
     * Batch UPDATE — đánh dấu tất cả thông báo chưa đọc của 1 recipient thành đã đọc.
     * Dùng @Modifying + JPQL thay vì load từng bản ghi (tránh N+1).
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.recipient.id = :recipientId AND n.isRead = false")
    int markAllReadByRecipientId(@Param("recipientId") Long recipientId);
}
