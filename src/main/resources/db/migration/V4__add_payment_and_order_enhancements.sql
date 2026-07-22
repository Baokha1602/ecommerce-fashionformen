-- =========================================================================
-- V4: Bổ sung schema cho thanh toán, tracking coupon, optimistic locking
-- =========================================================================

-- Bảng lưu lịch sử sử dụng coupon (coupon nào dùng trong đơn nào)
CREATE TABLE IF NOT EXISTS `coupon_usage_history` (
    `id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `coupon_id` BIGINT NOT NULL,
    `order_id`  BIGINT NOT NULL,
    `user_id`   BIGINT NOT NULL,
    `used_at`   DATETIME(6) NOT NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Thêm cột version vào product_variants cho optimistic locking backup
ALTER TABLE `product_variants` ADD COLUMN IF NOT EXISTS `version` BIGINT NOT NULL DEFAULT 0;

-- Thêm cột coupon_code vào orders để lưu text mã coupon đã dùng
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `coupon_code` VARCHAR(50) DEFAULT NULL;

-- Index cho truy vấn coupon usage
CREATE INDEX `idx_coupon_usage_coupon` ON `coupon_usage_history` (`coupon_id`);
CREATE INDEX `idx_coupon_usage_order` ON `coupon_usage_history` (`order_id`);
