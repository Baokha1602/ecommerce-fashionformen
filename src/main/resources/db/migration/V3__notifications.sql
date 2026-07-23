-- =========================================================================
-- V3: TẠO BẢNG NOTIFICATIONS
-- Lưu thông báo cho ADMIN và STAFF về các sự kiện đơn hàng quan trọng
-- =========================================================================

CREATE TABLE `notifications` (
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipient_id` BIGINT NOT NULL,
    `type`         VARCHAR(50)  NOT NULL,
    `title`        VARCHAR(255) NOT NULL,
    `body`         TEXT         NOT NULL,
    `reference_id` BIGINT       DEFAULT NULL,
    `is_read`      BIT(1)       NOT NULL DEFAULT b'0',
    `created_at`   DATETIME(6)  NOT NULL,
    `updated_at`   DATETIME(6)  NOT NULL,
    CONSTRAINT `fk_notif_recipient`
        FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Index để truy vấn nhanh: lấy thông báo của 1 user + lọc chưa đọc
CREATE INDEX `idx_notif_recipient_read` ON `notifications` (`recipient_id`, `is_read`);

-- Index để sort theo thời gian tạo (mới nhất trước)
CREATE INDEX `idx_notif_created_at` ON `notifications` (`created_at`);
