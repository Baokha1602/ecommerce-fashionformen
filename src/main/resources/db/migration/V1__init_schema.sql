-- =========================================================================
-- V1: KHỞI TẠO TOÀN BỘ SCHEMA
-- Bao gồm toàn bộ cấu trúc bảng, các cột xóa mềm (is_deleted, deleted_at),
-- bảng notifications và các chỉ mục (indexes).
-- =========================================================================

-- =========================================================================
-- BƯỚC 1: CÁC BẢNG ĐỘC LẬP (Không chứa khóa ngoại tham chiếu bảng khác)
-- =========================================================================

-- Rank: khớp với Rank.java (extends AuditableEntity)
-- RankName enum: BRONZE, SILVER, GOLD, DIAMOND
CREATE TABLE `ranks` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `rank_name`     ENUM('BRONZE', 'SILVER', 'GOLD', 'DIAMOND') NOT NULL UNIQUE,
    `point`         INT NOT NULL DEFAULT 0,
    `rank_discount` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Coupon: khớp với Coupon.java (extends AuditableEntity)
CREATE TABLE `coupons` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code`                VARCHAR(50) NOT NULL UNIQUE,
    `name`                VARCHAR(255) NOT NULL,
    `discount_rate`       DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `max_discount_amount` DECIMAL(19,4) DEFAULT NULL,
    `min_order_value`     DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `start_date`          DATETIME(6) NOT NULL,
    `end_date`            DATETIME(6) NOT NULL,
    `usage_limit`         INT NOT NULL DEFAULT 1,
    `is_active`           BIT(1) DEFAULT b'1',
    `is_deleted`          BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`          DATETIME(6) DEFAULT NULL,
    `created_at`          DATETIME(6) NOT NULL,
    `updated_at`          DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Tag: khớp với Tag.java (extends AuditableEntity)
CREATE TABLE `tags` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL,
    `is_deleted`  BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`  DATETIME(6) DEFAULT NULL,
    `created_at`  DATETIME(6) NOT NULL,
    `updated_at`  DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Promotion: khớp với Promotion.java (extends AuditableEntity)
CREATE TABLE `promotions` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `start_date`  DATETIME(6) NOT NULL,
    `end_date`    DATETIME(6) NOT NULL,
    `is_active`   BIT(1) DEFAULT b'1',
    `is_deleted`  BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`  DATETIME(6) DEFAULT NULL,
    `created_at`  DATETIME(6) NOT NULL,
    `updated_at`  DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Category: khớp với Category.java (extends AuditableEntity)
CREATE TABLE `categories` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(100) NOT NULL UNIQUE,
    `is_deleted` BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at` DATETIME(6) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Brand: khớp với Brand.java (extends AuditableEntity)
CREATE TABLE `brands` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255) DEFAULT NULL,
    `logo_url`    VARCHAR(500) DEFAULT NULL,
    `is_active`   BIT(1) DEFAULT b'1',
    `is_deleted`  BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`  DATETIME(6) DEFAULT NULL,
    `created_at`  DATETIME(6) NOT NULL,
    `updated_at`  DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Banner: khớp với Banner.java (extends AuditableEntity)
CREATE TABLE `banners` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title`         VARCHAR(150) DEFAULT NULL,
    `image_url`     VARCHAR(500) NOT NULL,
    `link_url`      VARCHAR(500) DEFAULT NULL,
    `display_order` INT NOT NULL DEFAULT 0,
    `is_active`     BIT(1) DEFAULT b'1',
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Otp: khớp với Otp.java (extends AuditableEntity)
-- OtpPurpose enum: REGISTER, LOGIN, RESET_PASSWORD
CREATE TABLE `otps` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `otp_code`            VARCHAR(6) NOT NULL,
    `email`               VARCHAR(100) NOT NULL,
    `expires_at`          DATETIME(6) NOT NULL,
    `is_used`             BIT(1) DEFAULT b'0',
    `purpose`             ENUM('REGISTER', 'LOGIN', 'RESET_PASSWORD') NOT NULL,
    `failed_otp_attempts` INT DEFAULT 0,
    `is_deleted`          BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`          DATETIME(6) DEFAULT NULL,
    `created_at`          DATETIME(6) NOT NULL,
    `updated_at`          DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- EAV: Attribute (tên thuộc tính, vd: Màu sắc, Size)
CREATE TABLE `attributes` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(255) NOT NULL,
    `is_deleted` BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at` DATETIME(6) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- EAV: AttributeValue (giá trị thuộc tính, vd: Đỏ, XL)
CREATE TABLE `attribute_values` (
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
    `attribute_id` BIGINT NOT NULL,
    `value`        VARCHAR(255) NOT NULL,
    `is_deleted`   BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`   DATETIME(6) DEFAULT NULL,
    `created_at`   DATETIME(6) NOT NULL,
    `updated_at`   DATETIME(6) NOT NULL,
    CONSTRAINT `fk_attribute_value_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 2: CÁC BẢNG PHỤ THUỘC CẤP 1
-- =========================================================================

-- User: khớp với User.java (extends AuditableEntity)
-- UserRole enum: ADMIN, CUSTOMER, STAFF
CREATE TABLE `users` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`      VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `email`         VARCHAR(100) DEFAULT NULL,
    `phone`         VARCHAR(20) NOT NULL UNIQUE,
    `full_name`     VARCHAR(255) NOT NULL,
    `avatar_image`  VARCHAR(500) DEFAULT NULL,
    `date_of_birth` DATE DEFAULT NULL,
    `user_role`     ENUM('ADMIN', 'CUSTOMER', 'STAFF') NOT NULL DEFAULT 'CUSTOMER',
    `rank_id`       BIGINT DEFAULT NULL,
    `current_point` INT NOT NULL DEFAULT 0,
    `is_active`     BIT(1) NOT NULL DEFAULT b'1',
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL,
    FOREIGN KEY (`rank_id`) REFERENCES `ranks` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Product: khớp với Product.java (extends AuditableEntity)
CREATE TABLE `products` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_id`   BIGINT NOT NULL,
    `brand_id`      BIGINT DEFAULT NULL,
    `name`          VARCHAR(255) NOT NULL,
    `description`   TEXT DEFAULT NULL,
    `sold_quantity` INT NOT NULL DEFAULT 0,
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL,
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
    FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Notifications: khớp với Notification.java (extends AuditableEntity)
CREATE TABLE `notifications` (
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipient_id` BIGINT NOT NULL,
    `type`         VARCHAR(50)  NOT NULL,
    `title`        VARCHAR(255) NOT NULL,
    `body`         TEXT         NOT NULL,
    `reference_id` BIGINT       DEFAULT NULL,
    `is_read`      BIT(1)       NOT NULL DEFAULT b'0',
    `is_deleted`   BIT(1)       NOT NULL DEFAULT b'0',
    `deleted_at`   DATETIME(6)  DEFAULT NULL,
    `created_at`   DATETIME(6)  NOT NULL,
    `updated_at`   DATETIME(6)  NOT NULL,
    CONSTRAINT `fk_notif_recipient`
        FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 3: CÁC BẢNG PHỤ THUỘC CẤP 2
-- =========================================================================

-- UserAddress: khớp với UserAddress.java (extends AuditableEntity)
-- AddressType enum: HOME, OFFICE, OTHER
CREATE TABLE `user_address` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT NOT NULL,
    `address`       VARCHAR(255) NOT NULL,
    `address_type`  ENUM('HOME', 'OFFICE', 'OTHER') NOT NULL DEFAULT 'HOME',
    `province_id`   BIGINT NOT NULL,
    `province_name` VARCHAR(255) NOT NULL,
    `district_id`   BIGINT NOT NULL,
    `district_name` VARCHAR(255) NOT NULL,
    `ward_id`       VARCHAR(50) NOT NULL,
    `ward_name`     VARCHAR(255) NOT NULL,
    `is_default`    BIT(1) DEFAULT b'0',
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- RefreshTokenSession: khớp với RefreshTokenSession.java (extends AuditableEntity)
CREATE TABLE `refresh_token_sessions` (
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY,
    `jti`              VARCHAR(100) NOT NULL UNIQUE,
    `user_id`          BIGINT NOT NULL,
    `expires_at`       DATETIME(6) NOT NULL,
    `revoked_at`       DATETIME(6) DEFAULT NULL,
    `replaced_by_jti`  VARCHAR(100) DEFAULT NULL,
    `is_deleted`       BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`       DATETIME(6) DEFAULT NULL,
    `created_at`       DATETIME(6) NOT NULL,
    `updated_at`       DATETIME(6) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ProductVariant: khớp với ProductVariant.java (extends AuditableEntity)
CREATE TABLE `product_variants` (
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id`     BIGINT NOT NULL,
    `name`           VARCHAR(255) NOT NULL,
    `price`          DECIMAL(19,4) NOT NULL,
    `discount_price` DECIMAL(19,4) DEFAULT NULL,
    `discount_rate`  DECIMAL(5,2) DEFAULT NULL,
    `stock_total`    INT NOT NULL DEFAULT 0,
    `stock_lock`     INT NOT NULL DEFAULT 0,
    `version`        BIGINT NOT NULL DEFAULT 0,
    `is_deleted`     BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`     DATETIME(6) DEFAULT NULL,
    `created_at`     DATETIME(6) NOT NULL,
    `updated_at`     DATETIME(6) NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- VariantAttributeValue: EAV join table
CREATE TABLE `variant_attribute_values` (
    `variant_id`         BIGINT NOT NULL,
    `attribute_value_id` BIGINT NOT NULL,
    PRIMARY KEY (`variant_id`, `attribute_value_id`),
    CONSTRAINT `fk_variant_attribute_variant` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_variant_attribute_value`   FOREIGN KEY (`attribute_value_id`) REFERENCES `attribute_values` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- PromotionProduct: khớp với PromotionProduct.java (extends AuditableEntity)
CREATE TABLE `promotion_products` (
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    `promotion_id`       BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `discount_type`      ENUM('PERCENTAGE', 'FIXED') NOT NULL,
    `discount_value`     DECIMAL(19,4) NOT NULL,
    `is_deleted`         BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`         DATETIME(6) DEFAULT NULL,
    `created_at`         DATETIME(6) NOT NULL,
    `updated_at`         DATETIME(6) NOT NULL,
    FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uq_promotion_variant` (`promotion_id`, `product_variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ProductImage: khớp với ProductImages.java (extends AuditableEntity)
CREATE TABLE `product_images` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id`    BIGINT NOT NULL,
    `url_image`     VARCHAR(500) NOT NULL,
    `is_main_image` BIT(1) DEFAULT b'0',
    `is_deleted`    BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`    DATETIME(6) DEFAULT NULL,
    `created_at`    DATETIME(6) NOT NULL,
    `updated_at`    DATETIME(6) NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ProductTag: khớp với ProductTag.java (extends AuditableEntity)
CREATE TABLE `product_tags` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `tag_id`     BIGINT NOT NULL,
    `is_deleted` BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at` DATETIME(6) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ProductReview: khớp với ProductReviews.java (extends AuditableEntity)
CREATE TABLE `product_reviews` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `user_id`    BIGINT NOT NULL,
    `rating`     INT NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
    `title`      VARCHAR(255) DEFAULT NULL,
    `comment`    TEXT DEFAULT NULL,
    `is_deleted` BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at` DATETIME(6) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Cart: khớp với Cart.java (extends AuditableEntity)
CREATE TABLE `carts` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`             BIGINT NOT NULL UNIQUE,
    `applied_coupon_code` VARCHAR(255) DEFAULT NULL,
    `is_deleted`          BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`          DATETIME(6) DEFAULT NULL,
    `created_at`          DATETIME(6) NOT NULL,
    `updated_at`          DATETIME(6) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- BƯỚC 4: CÁC BẢNG HOÀN THIỆN LUỒNG GIAO DỊCH
-- =========================================================================

-- CartItem: khớp với CartItem.java (extends AuditableEntity)
CREATE TABLE `cart_items` (
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    `cart_id`            BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `quantity`           INT NOT NULL CHECK (`quantity` >= 1),
    `is_deleted`         BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`         DATETIME(6) DEFAULT NULL,
    `created_at`         DATETIME(6) NOT NULL,
    `updated_at`         DATETIME(6) NOT NULL,
    FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uq_cart_variant` (`cart_id`, `product_variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Order: khớp với Order.java (extends AuditableEntity)
CREATE TABLE `orders` (
    `id`                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`                 BIGINT DEFAULT NULL,
    `user_address_id`         BIGINT DEFAULT NULL,
    `coupon_id`               BIGINT DEFAULT NULL,
    `coupon_code`             VARCHAR(50) DEFAULT NULL,
    `first_name`              VARCHAR(100) NOT NULL,
    `last_name`               VARCHAR(50) NOT NULL,
    `phone_number`            VARCHAR(15) NOT NULL,
    `email`                   VARCHAR(100) DEFAULT NULL,
    `order_status`            ENUM('PENDING', 'PROCESSING', 'DELIVERING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    `payment_method`          ENUM('COD', 'MOMO', 'VN_PAY') DEFAULT 'COD',
    `is_paid`                 BIT(1) DEFAULT b'0',
    `subtotal_original`       DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `product_discount_amount` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `rank_discount_amount`    DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `coupon_discount_amount`  DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `shipping_fee_original`   DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `shipping_fee_actual`     DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `tax_amount`              DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `total_order_amount`      DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `final_amount`            DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `notes`                   VARCHAR(255) DEFAULT NULL,
    `is_deleted`              BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`              DATETIME(6) DEFAULT NULL,
    `created_at`              DATETIME(6) NOT NULL,
    `updated_at`              DATETIME(6) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`user_address_id`) REFERENCES `user_address` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- OrderItem: khớp với OrderItem.java (extends AuditableEntity)
CREATE TABLE `order_items` (
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id`           BIGINT NOT NULL,
    `product_variant_id` BIGINT NOT NULL,
    `price`              DECIMAL(19,4) NOT NULL,
    `quantity`           INT NOT NULL CHECK (`quantity` >= 1),
    `is_deleted`         BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`         DATETIME(6) DEFAULT NULL,
    `created_at`         DATETIME(6) NOT NULL,
    `updated_at`         DATETIME(6) NOT NULL,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- OrderStatusHistory: lưu lịch sử thay đổi trạng thái đơn hàng
CREATE TABLE `order_status_history` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id`   BIGINT NOT NULL,
    `status`     VARCHAR(50) NOT NULL,
    `changed_by` VARCHAR(255) DEFAULT NULL,
    `changed_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `note`       TEXT DEFAULT NULL,
    CONSTRAINT `fk_order_history_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Shipments: khớp với Shipment.java (extends AuditableEntity)
CREATE TABLE `shipments` (
    `id`                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id`               BIGINT NOT NULL UNIQUE,
    `ghn_order_code`         VARCHAR(100) NOT NULL,
    `expected_delivery_time` VARCHAR(50) DEFAULT NULL,
    `cod_amount`             DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    `total_weight`           INT DEFAULT NULL,
    `total_fee`              DECIMAL(19,4) DEFAULT NULL,
    `ghn_raw_response`       TEXT DEFAULT NULL,
    `is_deleted`             BIT(1) NOT NULL DEFAULT b'0',
    `deleted_at`             DATETIME(6) DEFAULT NULL,
    `created_at`             DATETIME(6) NOT NULL,
    `updated_at`             DATETIME(6) NOT NULL,
    CONSTRAINT `fk_shipments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- CouponUsageHistory: lịch sử sử dụng coupon
CREATE TABLE `coupon_usage_history` (
    `id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `coupon_id` BIGINT NOT NULL,
    `order_id`  BIGINT NOT NULL,
    `user_id`   BIGINT NOT NULL,
    `used_at`   DATETIME(6) NOT NULL,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================================================
-- ĐÁNH CHỈ MỤC TỐI ƯU HÓA TRUY VẤN
-- =========================================================================
CREATE INDEX `idx_users_username`       ON `users` (`username`);
CREATE INDEX `idx_products_category`    ON `products` (`category_id`);
CREATE INDEX `idx_products_brand`       ON `products` (`brand_id`);
CREATE INDEX `idx_products_sold`        ON `products` (`sold_quantity` DESC);
CREATE INDEX `idx_variants_product`     ON `product_variants` (`product_id`);
CREATE INDEX `idx_orders_user_status`   ON `orders` (`user_id`, `order_status`);
CREATE INDEX `idx_order_items_order`    ON `order_items` (`order_id`);
CREATE INDEX `idx_promotions_date`      ON `promotions` (`start_date`, `end_date`);
CREATE INDEX `idx_coupons_date`         ON `coupons` (`start_date`, `end_date`);
CREATE INDEX `idx_banners_order`        ON `banners` (`display_order`, `is_active`);
CREATE INDEX `idx_refresh_token_jti`    ON `refresh_token_sessions` (`jti`);
CREATE INDEX `idx_refresh_token_user`   ON `refresh_token_sessions` (`user_id`);
CREATE INDEX `idx_coupon_usage_coupon`  ON `coupon_usage_history` (`coupon_id`);
CREATE INDEX `idx_coupon_usage_order`   ON `coupon_usage_history` (`order_id`);
CREATE INDEX `idx_notif_recipient_read` ON `notifications` (`recipient_id`, `is_read`);
CREATE INDEX `idx_notif_created_at`     ON `notifications` (`created_at`);