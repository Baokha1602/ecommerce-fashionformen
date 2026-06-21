-- =========================================================================
-- BƯỚC 1: CÁC BẢNG ĐỘC LẬP (Không chứa khóa ngoại tham chiếu bảng khác)
-- =========================================================================

-- Cố định giảm giá theo % (DECIMAL(5,2)) để lưu giá trị từ 0.00 - 100.00
CREATE TABLE `ranks` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `rank_name` ENUM('BRONZE', 'SILVER', 'GOLD', 'DIAMOND') NOT NULL UNIQUE,
    `point` INT NOT NULL DEFAULT 0,
    `rank_discount` DECIMAL(5,2) NOT NULL DEFAULT 0.00 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ĐÃ ĐỔI TÊN: Bảng Coupon độc lập (Phục vụ chiến dịch Marketing nhập mã giảm %)
CREATE TABLE `coupons` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,                        -- Ví dụ: SUMMER20, FASHION50
    `name` VARCHAR(255) NOT NULL,
    `discount_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00,       -- Giảm theo % cố định (ví dụ: 10.00 là 10%)
    `max_discount_amount` DECIMAL(19,4) DEFAULT NULL,          -- Số tiền giảm tối đa (ví dụ: Giảm 20% tối đa 50k)
    `min_order_value` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,   -- Điều kiện giá trị đơn hàng tối thiểu để áp mã
    `start_date` DATETIME(6) NOT NULL,
    `end_date` DATETIME(6) NOT NULL,
    `usage_limit` INT NOT NULL DEFAULT 1,                     -- Tổng số lần mã này được sử dụng toàn hệ thống
    `is_active` BIT(1) DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tags` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `promotions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `start_date` DATETIME(6) NOT NULL,
    `end_date` DATETIME(6) NOT NULL,
    `is_active` BIT(1) DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `parent_id` BIGINT DEFAULT NULL,
    FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `brands` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL,
    `logo_url` VARCHAR(500) DEFAULT NULL,
    `is_active` BIT(1) DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `banners` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) DEFAULT NULL,
    `image_url` VARCHAR(500) NOT NULL,
    `link_url` VARCHAR(500) DEFAULT NULL,
    `display_order` INT NOT NULL DEFAULT 0,
    `is_active` BIT(1) DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `otps` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `otp_code` VARCHAR(6) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `expires_at` DATETIME(6) NOT NULL,
    `is_used` BIT(1) DEFAULT b'0',
    `purpose` ENUM('REGISTER', 'LOGIN', 'RESET_PASSWORD') NOT NULL,
    `failed_otp_attempts` INT DEFAULT 0,
    `reset_password_token` VARCHAR(255) DEFAULT NULL,
    `create_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 2: CÁC BẢNG PHỤ THUỘC CẤP 1
-- =========================================================================

CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_name` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `phone_number` VARCHAR(15) NOT NULL UNIQUE,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `gender` ENUM('MALE', 'FEMALE') NOT NULL,
    `date_of_birth` DATE DEFAULT NULL,
    `user_role` ENUM('CUSTOMER', 'STAFF', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    `rank_id` BIGINT DEFAULT NULL,
    `current_point` INT NOT NULL DEFAULT 0,
    FOREIGN KEY (`rank_id`) REFERENCES `ranks` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_id` BIGINT NOT NULL,
    `brand_id` BIGINT DEFAULT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `sold_quantity` INT NOT NULL DEFAULT 0,
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
    FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 3: CÁC BẢNG PHỤ THUỘC CẤP 2
-- =========================================================================

CREATE TABLE `user_address` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `address_type` ENUM('HOME', 'OFFICE', 'OTHER') NOT NULL DEFAULT 'HOME',
    `province_id` BIGINT NOT NULL,
    `province_name` VARCHAR(255) NOT NULL,
    `district_id` BIGINT NOT NULL,
    `district_name` VARCHAR(255) NOT NULL,
    `ward_id` VARCHAR(50) NOT NULL,
    `ward_name` VARCHAR(255) NOT NULL,
    `is_default` BIT(1) DEFAULT b'0',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `product_variants` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `price` DECIMAL(19,4) NOT NULL,
    `discount_price` DECIMAL(19,4) DEFAULT NULL, 
    `discount_rate` DECIMAL(5,2) DEFAULT NULL, 
    `stock_total` INT NOT NULL DEFAULT 0,
    `stock_lock` INT NOT NULL DEFAULT 0,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `promotion_products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `promotion_id` BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `discount_type` ENUM('PERCENTAGE', 'FIXED') NOT NULL,
    `discount_value` DECIMAL(19,4) NOT NULL,
    FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uq_promotion_variant` (`promotion_id`, `product_variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `product_images` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `url_image` VARCHAR(500) NOT NULL,
    `is_main_image` BIT(1) DEFAULT b'0',
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `product_tags` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `product_reviews` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `rating` INT NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
    `title` VARCHAR(255) DEFAULT NULL,
    `comment` TEXT DEFAULT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `carts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 4: CÁC BẢNG HOÀN THIỆN LUỒNG GIAO DỊCH
-- =========================================================================

CREATE TABLE `cart_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `cart_id` BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL CHECK (`quantity` >= 1),
    FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uq_cart_variant` (`cart_id`, `product_variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ĐÃ TỐI ƯU: Đổi sang liên kết coupon_id và lưu vết số tiền riêng biệt (Chọn 1 trong 2)
CREATE TABLE `orders` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT DEFAULT NULL,
    `user_address_id` BIGINT DEFAULT NULL,
    `coupon_id` BIGINT DEFAULT NULL,                                -- Đổi từ voucher_id sang coupon_id
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `phone_number` VARCHAR(15) NOT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `order_status` ENUM('PENDING', 'PROCESSING', 'DELIVERING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    `payment_method` ENUM('COD', 'MOMO', 'VN_PAY') DEFAULT 'COD',
    `is_paid` BIT(1) DEFAULT b'0',
    `subtotal_original` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `product_discount_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000, -- Số tiền giảm từ sản phẩm đang chạy Promotion sale
    `rank_discount_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,    -- Số tiền giảm từ Rank (Bằng 0 nếu khách chọn áp Coupon)
    `coupon_discount_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,  -- Số tiền giảm từ Coupon (Bằng 0 nếu khách chọn áp Rank)
    `shipping_fee_original` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `shipping_fee_actual` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `tax_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `total_order_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `final_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,           -- Số tiền cuối cùng sau khi đã loại trừ 1 trong 2
    `notes` VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`user_address_id`) REFERENCES `user_address` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `price` DECIMAL(19,4) NOT NULL,
    `quantity` INT NOT NULL CHECK (`quantity` >= 1),
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- ĐÁNH CHỈ MỤC LOGIC TỐI ƯU HÓA TRUY VẤN
-- =========================================================================
CREATE INDEX `idx_users_username` ON `users` (`user_name`);
CREATE INDEX `idx_products_category` ON `products` (`category_id`);
CREATE INDEX `idx_products_brand` ON `products` (`brand_id`);
CREATE INDEX `idx_products_sold` ON `products` (`sold_quantity` DESC);
CREATE INDEX `idx_variants_product` ON `product_variants` (`product_id`);
CREATE INDEX `idx_orders_user_status` ON `orders` (`user_id`, `order_status`);
CREATE INDEX `idx_order_items_order` ON `order_items` (`order_id`);
CREATE INDEX `idx_promotions_date` ON `promotions` (`start_date`, `end_date`);
CREATE INDEX `idx_coupons_date` ON `coupons` (`start_date`, `end_date`);        -- Đã cập nhật index theo bảng mới
CREATE INDEX `idx_banners_order` ON `banners` (`display_order`, `is_active`);