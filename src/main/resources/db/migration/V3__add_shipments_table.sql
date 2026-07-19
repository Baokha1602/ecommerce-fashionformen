-- =========================================================================
-- V3: Thêm bảng shipments để lưu thông tin vận đơn GHN
-- Được tạo khi Admin kích hoạt giao hàng (PUT /api/orders/{id}/deliver)
-- =========================================================================

CREATE TABLE `shipments` (
    `id`                     BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Liên kết với đơn hàng (1 đơn hàng = 1 vận đơn)
    `order_id`               BIGINT NOT NULL UNIQUE,

    -- Mã vận đơn do GHN cấp
    `ghn_order_code`         VARCHAR(100) NOT NULL,

    -- Thời gian dự kiến giao hàng (chuỗi ISO-8601 từ GHN)
    `expected_delivery_time` VARCHAR(50) DEFAULT NULL,

    -- Tiền COD shipper thu (0 nếu đã thanh toán online)
    `cod_amount`             DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,

    -- Cân nặng tổng kiện hàng (gram)
    `total_weight`           INT DEFAULT NULL,

    -- Phí vận chuyển thực tế GHN tính
    `total_fee`              DECIMAL(19, 4) DEFAULT NULL,

    -- Raw JSON response từ GHN để audit/debug
    `ghn_raw_response`       TEXT DEFAULT NULL,

    `created_at`             DATETIME(6) NOT NULL,
    `updated_at`             DATETIME(6) NOT NULL,

    CONSTRAINT `fk_shipments_order`
        FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
        ON DELETE CASCADE

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
