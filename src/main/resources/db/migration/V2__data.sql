-- =========================================================================
-- V2: DỮ LIỆU MẪU (SEED DATA)
-- Thứ tự insert phải khớp với thứ tự phụ thuộc khóa ngoại trong V1
-- Mật khẩu demo tất cả user: 123456
-- =========================================================================

-- -------------------------------------------------------------------------
-- 1. RANKS (Rank.java)
--    RankName enum: BRONZE, SILVER, GOLD, DIAMOND
-- -------------------------------------------------------------------------
INSERT INTO `ranks` (`rank_name`, `point`, `rank_discount`, `created_at`, `updated_at`) VALUES
('BRONZE',  0,    0.00,  NOW(6), NOW(6)),
('SILVER',  500,  5.00,  NOW(6), NOW(6)),
('GOLD',    2000, 10.00, NOW(6), NOW(6)),
('DIAMOND', 5000, 15.00, NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 2. BRANDS (Brand.java)
-- -------------------------------------------------------------------------
INSERT INTO `brands` (`name`, `description`, `logo_url`, `is_active`, `created_at`, `updated_at`) VALUES
('Nike',     'Thương hiệu thể thao hàng đầu thế giới từ Mỹ.',           'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg',         b'1', NOW(6), NOW(6)),
('Adidas',   'Thương hiệu thể thao nổi tiếng toàn cầu từ Đức.',         'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg',        b'1', NOW(6), NOW(6)),
('Zara',     'Thời trang nhanh cao cấp từ Tây Ban Nha.',                 'https://upload.wikimedia.org/wikipedia/commons/f/fd/Zara_Logo.svg',           b'1', NOW(6), NOW(6)),
('H&M',      'Thời trang bình dân phổ biến từ Thụy Điển.',              'https://upload.wikimedia.org/wikipedia/commons/5/53/H%26M-Logo.svg',          b'1', NOW(6), NOW(6)),
('Routine',  'Thương hiệu thời trang nam nội địa Việt Nam hiện đại.',   'https://routineclothes.com/wp-content/uploads/2023/01/routine-logo-dark.png', b'1', NOW(6), NOW(6)),
('BILUXURY', 'Thương hiệu suit & vest cao cấp dành riêng cho nam.',     'https://biluxury.vn/wp-content/uploads/2022/07/logo-biluxury.png',            b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 3. CATEGORIES (Category.java)
-- -------------------------------------------------------------------------
INSERT INTO `categories` (`name`, `created_at`, `updated_at`) VALUES
('Áo',             NOW(6), NOW(6)),
('Quần',           NOW(6), NOW(6)),
('Giày & Dép',     NOW(6), NOW(6)),
('Phụ kiện',       NOW(6), NOW(6)),
('Áo Thun',        NOW(6), NOW(6)),
('Áo Sơ Mi',       NOW(6), NOW(6)),
('Áo Khoác',       NOW(6), NOW(6)),
('Áo Hoodie',      NOW(6), NOW(6)),
('Quần Jeans',     NOW(6), NOW(6)),
('Quần Short',     NOW(6), NOW(6)),
('Quần Tây',       NOW(6), NOW(6)),
('Quần Jogger',    NOW(6), NOW(6)),
('Giày Sneaker',   NOW(6), NOW(6)),
('Giày Tây',       NOW(6), NOW(6)),
('Dép Sandal',     NOW(6), NOW(6)),
('Thắt Lưng',      NOW(6), NOW(6)),
('Mũ & Nón',       NOW(6), NOW(6)),
('Túi & Ba Lô',    NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 4. TAGS (Tag.java)
-- -------------------------------------------------------------------------
INSERT INTO `tags` (`name`, `description`, `created_at`, `updated_at`) VALUES
('Sale',            'Sản phẩm đang giảm giá.',                       NOW(6), NOW(6)),
('Hàng Mới Về',     'Sản phẩm mới về.',                              NOW(6), NOW(6)),
('Bán Chạy',        'Sản phẩm bán chạy nhất.',                       NOW(6), NOW(6)),
('Phiên Bản Giới Hạn', 'Phiên bản giới hạn, số lượng có hạn.',      NOW(6), NOW(6)),
('Mùa Hè',          'Phù hợp cho mùa hè.',                           NOW(6), NOW(6)),
('Mùa Đông',        'Phù hợp cho mùa đông.',                         NOW(6), NOW(6)),
('Năng Động',       'Phong cách năng động, thoải mái hàng ngày.',     NOW(6), NOW(6)),
('Lịch Sự',         'Phong cách lịch sự, phù hợp công sở.',          NOW(6), NOW(6)),
('Đường Phố',       'Phong cách đường phố cá tính.',                  NOW(6), NOW(6)),
('Thể Thao',        'Trang phục thể thao năng động.',                 NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 5. BANNERS (Banner.java)
-- -------------------------------------------------------------------------
INSERT INTO `banners` (`title`, `image_url`, `link_url`, `display_order`, `is_active`, `created_at`, `updated_at`) VALUES
('Summer Sale 2026 - Giảm đến 50%',     'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200', '/san-pham?tag=sale',        1, b'1', NOW(6), NOW(6)),
('Bộ Sưu Tập Mới - Hàng Mới Về',       'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200', '/san-pham?tag=hang-moi-ve', 2, b'1', NOW(6), NOW(6)),
('Hàng Mới Về Mỗi Tuần',               'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=1200', '/san-pham',                 3, b'1', NOW(6), NOW(6)),
('Phong Cách Nam Tính - BILUXURY Suit', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1200', '/thuong-hieu/biluxury',     4, b'1', NOW(6), NOW(6)),
('Flash Sale Cuối Tuần',                'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200', '/san-pham?tag=sale',        5, b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 6. COUPONS (Coupon.java)
-- -------------------------------------------------------------------------
INSERT INTO `coupons` (`code`, `name`, `discount_rate`, `max_discount_amount`, `min_order_value`, `start_date`, `end_date`, `usage_limit`, `is_active`, `created_at`, `updated_at`) VALUES
('WELCOME10',  'Mừng khách hàng mới - Giảm 10%',         10.00,  50000.0000, 100000.0000, '2026-01-01 00:00:00.000000', '2026-12-31 23:59:59.000000', 1000, b'1', NOW(6), NOW(6)),
('SUMMER20',   'Sale Hè 2026 - Giảm 20%',                 20.00, 100000.0000, 300000.0000, '2026-06-01 00:00:00.000000', '2026-08-31 23:59:59.000000',  500, b'1', NOW(6), NOW(6)),
('FLASH50',    'Flash Sale Cuối Tuần - Giảm 50%',         50.00, 150000.0000, 500000.0000, '2026-07-14 00:00:00.000000', '2026-07-14 23:59:59.000000',  100, b'0', NOW(6), NOW(6)),
('VIP15',      'Ưu đãi thành viên VIP - Giảm 15%',        15.00, 200000.0000, 200000.0000, '2026-01-01 00:00:00.000000', '2026-12-31 23:59:59.000000', 2000, b'1', NOW(6), NOW(6)),
('BIRTHDAY30', 'Mừng sinh nhật - Giảm 30%',               30.00, 120000.0000, 150000.0000, '2026-07-01 00:00:00.000000', '2026-07-31 23:59:59.000000',  300, b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 7. USERS (User.java)
--    UserRole enum: ADMIN, CUSTOMER, STAFF
--    Mật khẩu demo cho tất cả user: 123456
-- -------------------------------------------------------------------------
INSERT INTO `users` (`username`, `password_hash`, `email`, `phone`, `full_name`, `avatar_url`, `date_of_birth`, `user_role`, `rank_id`, `current_point`, `is_active`, `created_at`, `updated_at`) VALUES
-- Admin (id=1)
('admin',       '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'admin@fashionformen.vn',     '0900000001', 'Nguyễn Quản Trị',  NULL,                               '1990-01-15', 'ADMIN',    4, 9999, b'1', NOW(6), NOW(6)),
-- Staff (id=2)
('staff_nam',   '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'nam.staff@fashionformen.vn', '0900000002', 'Trần Văn Nam',     NULL,                               '1995-05-20', 'STAFF',    1, 0,    b'1', NOW(6), NOW(6)),
-- Customers (id=3..7)
('baokha',       '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'baokhatranle@gmail.com',    '0901234567', 'Nguyễn Minh Hiếu', 'https://i.pravatar.cc/150?img=11', '1998-08-10', 'CUSTOMER', 2, 750,  b'1', NOW(6), NOW(6)),
('khachhang02', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'tuan.le@gmail.com',         '0912345678', 'Lê Hoàng Tuấn',    'https://i.pravatar.cc/150?img=12', '1997-03-22', 'CUSTOMER', 3, 2150, b'1', NOW(6), NOW(6)),
('khachhang03', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'phuc.tran@gmail.com',       '0923456789', 'Trần Quốc Phúc',   NULL,                               '2000-12-05', 'CUSTOMER', 1, 120,  b'1', NOW(6), NOW(6)),
('khachhang04', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'an.pham@yahoo.com',         '0934567890', 'Phạm Đức An',      'https://i.pravatar.cc/150?img=15', '1999-07-18', 'CUSTOMER', 1, 80,   b'1', NOW(6), NOW(6)),
('khachhang05', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'long.vo@gmail.com',         '0945678901', 'Võ Thành Long',    'https://i.pravatar.cc/150?img=16', '1996-02-28', 'CUSTOMER', 4, 6200, b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 8. USER_ADDRESS (UserAddress.java)
--    AddressType enum: HOME, OFFICE, OTHER
-- -------------------------------------------------------------------------
INSERT INTO `user_address` (`user_id`, `address`, `address_type`, `province_id`, `province_name`, `district_id`, `district_name`, `ward_id`, `ward_name`, `is_default`, `created_at`, `updated_at`) VALUES
-- khachhang01 (user_id=3)
(3, '12 Nguyễn Văn Bảo',   'HOME',   202, 'TP. Hồ Chí Minh', 1461, 'Quận Gò Vấp',     '21301',  'Phường 1',              b'1', NOW(6), NOW(6)),
(3, 'Tầng 5 Tòa nhà ABC',  'OFFICE', 202, 'TP. Hồ Chí Minh', 1462, 'Quận Bình Thạnh', '21608',  'Phường 12',             b'0', NOW(6), NOW(6)),
-- khachhang02 (user_id=4)
(4, '58 Lê Lợi',            'HOME',   201, 'Hà Nội',           1486, 'Quận Đống Đa',    '1A0421', 'Phường Văn Miếu',       b'1', NOW(6), NOW(6)),
-- khachhang03 (user_id=5)
(5, '99 Trần Phú',          'HOME',   203, 'Đà Nẵng',          1526, 'Quận Hải Châu',   '40103',  'Phường Hải Châu 1',     b'1', NOW(6), NOW(6)),
-- khachhang04 (user_id=6)
(6, '24 Hùng Vương',        'HOME',   202, 'TP. Hồ Chí Minh', 1442, 'Quận 1',          '20101',  'Phường Bến Nghé',       b'1', NOW(6), NOW(6)),
-- khachhang05 (user_id=7)
(7, '7 Pasteur',             'HOME',   202, 'TP. Hồ Chí Minh', 1442, 'Quận 1',          '20102',  'Phường Bến Thành',      b'1', NOW(6), NOW(6)),
(7, '15 Lý Tự Trọng',       'OFFICE', 202, 'TP. Hồ Chí Minh', 1442, 'Quận 1',          '20104',  'Phường Cầu Ông Lãnh',   b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 9. OTPS (Otp.java)
--    OtpPurpose enum: REGISTER, LOGIN, RESET_PASSWORD
--    Dữ liệu lịch sử (is_used = 1), trừ bản cuối chưa sử dụng
-- -------------------------------------------------------------------------
INSERT INTO `otps` (`otp_code`, `email`, `expires_at`, `is_used`, `purpose`, `failed_otp_attempts`, `created_at`, `updated_at`) VALUES
('472831', 'hieu.nguyen@gmail.com', '2026-07-10 10:05:00.000000', b'1', 'REGISTER',       0, '2026-07-10 10:00:00.000000', '2026-07-10 10:00:00.000000'),
('918273', 'tuan.le@gmail.com',     '2026-07-11 09:05:00.000000', b'1', 'LOGIN',           0, '2026-07-11 09:00:00.000000', '2026-07-11 09:00:00.000000'),
('334521', 'phuc.tran@gmail.com',   '2026-07-12 15:05:00.000000', b'1', 'RESET_PASSWORD',  1, '2026-07-12 15:00:00.000000', '2026-07-12 15:00:00.000000'),
('654892', 'an.pham@yahoo.com',     '2026-07-15 08:05:00.000000', b'1', 'REGISTER',        0, '2026-07-15 08:00:00.000000', '2026-07-15 08:00:00.000000'),
('123456', 'long.vo@gmail.com',     '2026-07-16 11:35:00.000000', b'0', 'LOGIN',            0, '2026-07-16 11:30:00.000000', '2026-07-16 11:30:00.000000');

-- -------------------------------------------------------------------------
-- 10. REFRESH_TOKEN_SESSIONS (RefreshTokenSession.java)
-- -------------------------------------------------------------------------
INSERT INTO `refresh_token_sessions` (`jti`, `user_id`, `expires_at`, `revoked_at`, `replaced_by_jti`, `created_at`, `updated_at`) VALUES
-- admin đang đăng nhập (user_id=1)
('a1b2c3d4-0001-0001-0001-aabbccddeeff', 1, '2026-07-23 04:30:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang01 phiên cũ đã thu hồi - token rotation (user_id=3)
('f1e2d3c4-0002-0002-0002-112233445566', 3, '2026-07-23 03:00:00.000000', '2026-07-16 10:00:00.000000', 'g1h2i3j4-0003-0003-0003-aabbccddeeff', '2026-07-16 03:00:00.000000', '2026-07-16 10:00:00.000000'),
-- khachhang01 phiên mới sau khi làm mới token (user_id=3)
('g1h2i3j4-0003-0003-0003-aabbccddeeff', 3, '2026-07-23 10:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang02 đang đăng nhập (user_id=4)
('b2c3d4e5-0004-0004-0004-99aabbccddee', 4, '2026-07-23 05:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang05 đang đăng nhập (user_id=7)
('c3d4e5f6-0005-0005-0005-8899aabbccdd', 7, '2026-07-23 06:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 11. PROMOTIONS (Promotion)
-- -------------------------------------------------------------------------
INSERT INTO `promotions` (`name`, `description`, `start_date`, `end_date`, `is_active`, `created_at`, `updated_at`) VALUES
('Chào Hè Rực Rỡ',    'Giảm giá các sản phẩm thời trang mùa hè',  '2026-06-01 00:00:00.000000', '2026-08-31 23:59:59.000000', b'1', NOW(6), NOW(6)),
('Flash Sale Tuần Lễ', 'Giảm giá sốc trong tuần',                  '2026-07-10 00:00:00.000000', '2026-07-20 23:59:59.000000', b'1', NOW(6), NOW(6)),
('Black Friday',       'Đại tiệc mua sắm giảm giá siêu sâu',       '2026-11-20 00:00:00.000000', '2026-11-30 23:59:59.000000', b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 12. PRODUCTS (Product)
--     category_id theo thứ tự categories insert: Áo Thun=5, Áo Sơ Mi=6, Áo Khoác=7, Quần Jeans=9, Giày Sneaker=13, Mũ & Nón=17
--     brand_id: Nike=1, Adidas=2, H&M=4, Routine=5, BILUXURY=6
-- -------------------------------------------------------------------------
INSERT INTO `products` (`category_id`, `brand_id`, `name`, `description`, `sold_quantity`, `created_at`, `updated_at`) VALUES
(5,  5, 'Áo Thun Nam Basic Routine',        'Áo thun cotton 100% thoáng mát, form màu sắc cơ bản dễ phối đồ.',    150, NOW(6), NOW(6)), -- id=1
(13, 1, 'Giày Thể Thao Nike Air Force 1',  'Huyền thoại sneaker từ Nike, thiết kế cổ điển, năng động.',           320, NOW(6), NOW(6)), -- id=2
(9,  4, 'Quần Jeans Nam Slimfit H&M',       'Quần jeans phom ôm vừa phải, chất liệu co giãn nhẹ, thoải mái.',     85,  NOW(6), NOW(6)), -- id=3
(6,  6, 'Áo Sơ Mi Trắng Công Sở BILUXURY', 'Áo sơ mi nam cao cấp, chống nhăn, phù hợp đi làm, đi tiệc.',        210, NOW(6), NOW(6)), -- id=4
(17, 2, 'Mũ Lưỡi Trai Adidas Thời Trang',  'Mũ thể thao Adidas chất liệu kaki cao cấp, thấm hút mồ hôi.',        45,  NOW(6), NOW(6)); -- id=5

-- -------------------------------------------------------------------------
-- 13. PRODUCT_VARIANTS (ProductVariant)
-- -------------------------------------------------------------------------
INSERT INTO `product_variants` (`product_id`, `name`, `price`, `discount_price`, `discount_rate`, `stock_total`, `stock_lock`, `created_at`, `updated_at`) VALUES
-- Áo thun Routine (product_id=1)
(1, 'Đen - Size M',         250000.0000, 200000.0000, 20.00, 100, 5, NOW(6), NOW(6)), -- id=1
(1, 'Trắng - Size L',       250000.0000, 200000.0000, 20.00, 100, 2, NOW(6), NOW(6)), -- id=2
-- Giày Nike (product_id=2)
(2, 'Trắng - Size 42',     2500000.0000,       NULL,  NULL,   50, 0, NOW(6), NOW(6)), -- id=3
(2, 'Đen - Size 43',       2500000.0000,       NULL,  NULL,   30, 1, NOW(6), NOW(6)), -- id=4
-- Quần Jeans H&M (product_id=3)
(3, 'Xanh Đậm - Size 32',   650000.0000, 585000.0000, 10.00, 200, 0, NOW(6), NOW(6)), -- id=5
-- Áo sơ mi BILUXURY (product_id=4)
(4, 'Trắng - Size 40',      450000.0000,       NULL,  NULL,  120,10, NOW(6), NOW(6)), -- id=6
-- Mũ Adidas (product_id=5)
(5, 'Đen - Freesize',       350000.0000, 300000.0000, 14.28,  80, 0, NOW(6), NOW(6)); -- id=7

-- -------------------------------------------------------------------------
-- 14. PRODUCT_IMAGES (ProductImage)
-- -------------------------------------------------------------------------
INSERT INTO `product_images` (`product_id`, `url_image`, `is_main_image`, `created_at`, `updated_at`) VALUES
(1, 'https://routine.vn/media/catalog/product/ao-thun-nam-1.jpg',                              b'1', NOW(6), NOW(6)),
(1, 'https://routine.vn/media/catalog/product/ao-thun-nam-2.jpg',                              b'0', NOW(6), NOW(6)),
(2, 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto/air-force-1.jpg',                  b'1', NOW(6), NOW(6)),
(3, 'https://lp2.hm.com/hmgoepprod?set=quality%5B79%5D%2Csource%5B%2Fjeans.jpg%5D',           b'1', NOW(6), NOW(6)),
(4, 'https://biluxury.vn/media/catalog/product/ao-so-mi-1.jpg',                                b'1', NOW(6), NOW(6)),
(5, 'https://assets.adidas.com/images/h_840,f_auto,q_auto/mu-adidas.jpg',                      b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 15. PRODUCT_TAGS (ProductTag)
--     Tags: Sale(1), Hàng Mới Về(2), Bán Chạy(3), Mùa Hè(5), Năng Động(7), Lịch Sự(8), Thể Thao(10)
-- -------------------------------------------------------------------------
INSERT INTO `product_tags` (`product_id`, `tag_id`, `created_at`, `updated_at`) VALUES
(1, 1,  NOW(6), NOW(6)), -- Áo thun: Sale
(1, 5,  NOW(6), NOW(6)), -- Áo thun: Mùa Hè
(1, 7,  NOW(6), NOW(6)), -- Áo thun: Năng Động
(2, 3,  NOW(6), NOW(6)), -- Giày Nike: Bán Chạy
(2, 10, NOW(6), NOW(6)), -- Giày Nike: Thể Thao
(3, 7,  NOW(6), NOW(6)), -- Quần Jeans: Năng Động
(4, 3,  NOW(6), NOW(6)), -- Áo sơ mi: Bán Chạy
(4, 8,  NOW(6), NOW(6)), -- Áo sơ mi: Lịch Sự
(5, 1,  NOW(6), NOW(6)), -- Mũ: Sale
(5, 10, NOW(6), NOW(6)); -- Mũ: Thể Thao

-- -------------------------------------------------------------------------
-- 16. PROMOTION_PRODUCTS (PromotionProduct)
--     promotion_id: Chào Hè=1, Flash Sale=2
-- -------------------------------------------------------------------------
INSERT INTO `promotion_products` (`promotion_id`, `product_variant_id`, `discount_type`, `discount_value`, `created_at`, `updated_at`) VALUES
(1, 1, 'PERCENTAGE', 20.0000,    NOW(6), NOW(6)), -- Hè: Áo thun Đen M
(1, 2, 'PERCENTAGE', 20.0000,    NOW(6), NOW(6)), -- Hè: Áo thun Trắng L
(2, 7, 'FIXED',      50000.0000, NOW(6), NOW(6)); -- Flash: Mũ Adidas (giảm cứng 50k)

-- -------------------------------------------------------------------------
-- 17. PRODUCT_REVIEWS (ProductReview)
-- -------------------------------------------------------------------------
INSERT INTO `product_reviews` (`product_id`, `user_id`, `rating`, `title`, `comment`, `created_at`, `updated_at`) VALUES
(2, 3, 5, 'Tuyệt vời!',       'Giày đẹp, đi rất êm chân, giao hàng nhanh.',              NOW(6), NOW(6)), -- khachhang01 đánh giá Nike
(4, 4, 4, 'Chất vải đẹp',     'Áo đúng form, mặc rất mát, tuy nhiên size hơi ôm.',       NOW(6), NOW(6)), -- khachhang02 đánh giá Áo sơ mi
(1, 5, 5, 'Ngon bổ rẻ',       'Giá hợp lý, mặc hàng ngày rất ổn.',                       NOW(6), NOW(6)); -- khachhang03 đánh giá Áo thun

-- -------------------------------------------------------------------------
-- 18. CARTS (Cart) - 1 User chỉ có 1 Cart
-- -------------------------------------------------------------------------
INSERT INTO `carts` (`user_id`, `created_at`, `updated_at`) VALUES
(3, NOW(6), NOW(6)), -- Giỏ hàng của khachhang01 (id=1)
(4, NOW(6), NOW(6)), -- Giỏ hàng của khachhang02 (id=2)
(7, NOW(6), NOW(6)); -- Giỏ hàng của khachhang05 (id=3)

-- -------------------------------------------------------------------------
-- 19. CART_ITEMS (CartItem)
-- -------------------------------------------------------------------------
INSERT INTO `cart_items` (`cart_id`, `product_variant_id`, `quantity`, `created_at`, `updated_at`) VALUES
(1, 1, 2, NOW(6), NOW(6)), -- khachhang01: 2 Áo thun đen
(1, 3, 1, NOW(6), NOW(6)), -- khachhang01: 1 Giày Nike trắng
(2, 6, 3, NOW(6), NOW(6)), -- khachhang02: 3 Áo sơ mi
(3, 5, 1, NOW(6), NOW(6)); -- khachhang05: 1 Quần jeans

-- -------------------------------------------------------------------------
-- 20. ORDERS (Order)
-- -------------------------------------------------------------------------
INSERT INTO `orders` (`user_id`, `user_address_id`, `coupon_id`, `coupon_code`, `first_name`, `last_name`, `phone_number`, `email`, `order_status`, `payment_method`, `is_paid`, `subtotal_original`, `product_discount_amount`, `rank_discount_amount`, `coupon_discount_amount`, `shipping_fee_original`, `shipping_fee_actual`, `tax_amount`, `total_order_amount`, `final_amount`, `notes`, `created_at`, `updated_at`) VALUES
-- Đơn hàng 1: khachhang02 (user 4) - 2 Áo Sơ Mi (2 x 450k = 900k)
-- Đang giao, Đã thanh toán VN_PAY, Dùng VIP15 giảm 15%
(4, 3, 4, 'VIP15', 'Lê Hoàng', 'Tuấn', '0912345678', 'tuan.le@gmail.com',
 'DELIVERING', 'VN_PAY', b'1',
 900000.0000, 0.0000, 45000.0000, 135000.0000, 30000.0000, 0.0000, 0.0000, 930000.0000, 750000.0000,
 'Giao giờ hành chính giúp mình', NOW(6), NOW(6)),

-- Đơn hàng 2: khachhang05 (user 7) - 1 Giày Nike (2.5tr)
-- Đã giao thành công, COD, Rank Diamond giảm 20% = 500k
(7, 6, NULL, NULL, 'Võ Thành', 'Long', '0945678901', 'long.vo@gmail.com',
 'DELIVERED', 'COD', b'1',
 2500000.0000, 0.0000, 500000.0000, 0.0000, 50000.0000, 50000.0000, 0.0000, 2550000.0000, 2050000.0000,
 'Gói hàng cẩn thận nhé', NOW(6), NOW(6)),

-- Đơn hàng 3: Khách vãng lai (NULL user) - 1 Áo Thun đen (variant 1 - giá gốc 250k giảm còn 200k)
-- Chờ xác nhận, COD
(NULL, NULL, NULL, NULL, 'Ngô Văn', 'Khách', '0988777666', 'khach123@gmail.com',
 'PENDING', 'COD', b'0',
 250000.0000, 50000.0000, 0.0000, 0.0000, 25000.0000, 25000.0000, 0.0000, 275000.0000, 225000.0000,
 NULL, NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 21. ORDER_ITEMS (OrderItem)
-- -------------------------------------------------------------------------
INSERT INTO `order_items` (`order_id`, `product_variant_id`, `price`, `quantity`, `created_at`, `updated_at`) VALUES
(1, 6, 450000.0000,  2, NOW(6), NOW(6)),  -- Đơn 1: 2 Áo sơ mi 450k
(2, 3, 2500000.0000, 1, NOW(6), NOW(6)),  -- Đơn 2: 1 Giày Nike 2.5tr
(3, 1, 200000.0000,  1, NOW(6), NOW(6));  -- Đơn 3: 1 Áo thun (giá đã giảm 200k)