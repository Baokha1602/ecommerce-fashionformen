-- Thêm cột applied_coupon_code vào bảng carts
ALTER TABLE carts ADD COLUMN IF NOT EXISTS applied_coupon_code VARCHAR(255) DEFAULT NULL;

-- Bảng lưu lịch sử trạng thái đơn hàng
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note TEXT,
    CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
