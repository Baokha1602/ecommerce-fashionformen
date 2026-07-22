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
('DIAMOND', 5000, 20.00, NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 2. BRANDS (Brand.java)
-- -------------------------------------------------------------------------
INSERT INTO `brands` (`name`, `description`, `logo_url`, `is_active`, `created_at`, `updated_at`) VALUES
('Nike',     'Thuong hieu the thao hang dau the gioi tu My.',         'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg',         b'1', NOW(6), NOW(6)),
('Adidas',   'Thuong hieu the thao noi tieng toan cau tu Duc.',       'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg',        b'1', NOW(6), NOW(6)),
('Zara',     'Thoi trang nhanh cao cap tu Tay Ban Nha.',              'https://upload.wikimedia.org/wikipedia/commons/f/fd/Zara_Logo.svg',           b'1', NOW(6), NOW(6)),
('H&M',      'Thoi trang binh dan pho bien tu Thuy Dien.',            'https://upload.wikimedia.org/wikipedia/commons/5/53/H%26M-Logo.svg',          b'1', NOW(6), NOW(6)),
('Routine',  'Thuong hieu thoi trang nam noi dia Viet Nam hien dai.', 'https://routineclothes.com/wp-content/uploads/2023/01/routine-logo-dark.png', b'1', NOW(6), NOW(6)),
('BILUXURY', 'Thuong hieu suit & vest cao cap danh rieng cho nam.',   'https://biluxury.vn/wp-content/uploads/2022/07/logo-biluxury.png',            b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 3. CATEGORIES (Category.java)
-- -------------------------------------------------------------------------
INSERT INTO `categories` (`name`, `created_at`, `updated_at`) VALUES
('Ao',          NOW(6), NOW(6)),
('Quan',        NOW(6), NOW(6)),
('Giay & Dep',  NOW(6), NOW(6)),
('Phu kien',    NOW(6), NOW(6)),
('Ao Thun',     NOW(6), NOW(6)),
('Ao So Mi',    NOW(6), NOW(6)),
('Ao Khoac',    NOW(6), NOW(6)),
('Ao Hoodie',   NOW(6), NOW(6)),
('Quan Jeans',  NOW(6), NOW(6)),
('Quan Short',  NOW(6), NOW(6)),
('Quan Tay',    NOW(6), NOW(6)),
('Quan Jogger', NOW(6), NOW(6)),
('Giay Sneaker',NOW(6), NOW(6)),
('Giay Tay',    NOW(6), NOW(6)),
('Dep Sandal',  NOW(6), NOW(6)),
('That Lung',   NOW(6), NOW(6)),
('Mu & Non',    NOW(6), NOW(6)),
('Tui & Ba Lo', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 4. TAGS (Tag.java)
-- -------------------------------------------------------------------------
INSERT INTO `tags` (`name`, `description`, `created_at`, `updated_at`) VALUES
('Sale',            'San pham dang giam gia.',                    NOW(6), NOW(6)),
('New Arrival',     'San pham moi ve.',                           NOW(6), NOW(6)),
('Best Seller',     'San pham ban chay nhat.',                    NOW(6), NOW(6)),
('Limited Edition', 'Phien ban gioi han, so luong co han.',       NOW(6), NOW(6)),
('Summer',          'Phu hop cho mua he.',                        NOW(6), NOW(6)),
('Winter',          'Phu hop cho mua dong.',                      NOW(6), NOW(6)),
('Casual',          'Phong cach nang dong, thoai mai hang ngay.', NOW(6), NOW(6)),
('Formal',          'Phong cach lich su, phu hop cong so.',       NOW(6), NOW(6)),
('Streetwear',      'Phong cach duong pho ca tinh.',              NOW(6), NOW(6)),
('Sport',           'Trang phuc the thao nang dong.',             NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 5. BANNERS (Banner.java)
-- -------------------------------------------------------------------------
INSERT INTO `banners` (`title`, `image_url`, `link_url`, `display_order`, `is_active`, `created_at`, `updated_at`) VALUES
('Summer Sale 2026 - Giam den 50%',     'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200', '/san-pham?tag=sale',        1, b'1', NOW(6), NOW(6)),
('Bo Suu Tap Moi - New Arrival',        'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200', '/san-pham?tag=new-arrival', 2, b'1', NOW(6), NOW(6)),
('Hang Moi Ve Moi Tuan',                'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=1200', '/san-pham',                 3, b'1', NOW(6), NOW(6)),
('Phong Cach Nam Tinh - BILUXURY Suit', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1200', '/thuong-hieu/biluxury',     4, b'1', NOW(6), NOW(6)),
('Flash Sale Cuoi Tuan',                'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200', '/san-pham?tag=sale',        5, b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 6. COUPONS (Coupon.java)
-- -------------------------------------------------------------------------
INSERT INTO `coupons` (`code`, `name`, `discount_rate`, `max_discount_amount`, `min_order_value`, `start_date`, `end_date`, `usage_limit`, `is_active`, `created_at`, `updated_at`) VALUES
('WELCOME10',  'Mung khach hang moi - Giam 10%',      10.00,  50000.0000, 100000.0000, '2026-01-01 00:00:00.000000', '2026-12-31 23:59:59.000000', 1000, b'1', NOW(6), NOW(6)),
('SUMMER20',   'Sale He 2026 - Giam 20%',              20.00, 100000.0000, 300000.0000, '2026-06-01 00:00:00.000000', '2026-08-31 23:59:59.000000',  500, b'1', NOW(6), NOW(6)),
('FLASH50',    'Flash Sale Cuoi Tuan - Giam 50%',      50.00, 150000.0000, 500000.0000, '2026-07-14 00:00:00.000000', '2026-07-14 23:59:59.000000',  100, b'0', NOW(6), NOW(6)),
('VIP15',      'Uu dai thanh vien VIP - Giam 15%',     15.00, 200000.0000, 200000.0000, '2026-01-01 00:00:00.000000', '2026-12-31 23:59:59.000000', 2000, b'1', NOW(6), NOW(6)),
('BIRTHDAY30', 'Mung sinh nhat - Giam 30%',            30.00, 120000.0000, 150000.0000, '2026-07-01 00:00:00.000000', '2026-07-31 23:59:59.000000',  300, b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 7. USERS (User.java)
--    UserRole enum: ADMIN, CUSTOMER, STAFF
--    Mat khau demo cho tat ca user: 123456
-- -------------------------------------------------------------------------
INSERT INTO `users` (`username`, `password_hash`, `email`, `phone`, `full_name`, `avatar_url`, `date_of_birth`, `user_role`, `rank_id`, `current_point`, `is_active`, `created_at`, `updated_at`) VALUES
-- Admin (id=1)
('admin',       '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'admin@fashionformen.vn',     '0900000001', 'Nguyen Quan Tri',  NULL,                               '1990-01-15', 'ADMIN',    4, 9999, b'1', NOW(6), NOW(6)),
-- Staff (id=2)
('staff_nam',   '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'nam.staff@fashionformen.vn', '0900000002', 'Tran Van Nam',     NULL,                               '1995-05-20', 'STAFF',    1, 0,    b'1', NOW(6), NOW(6)),
-- Customers (id=3..7)
('khachhang01', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'hieu.nguyen@gmail.com',     '0901234567', 'Nguyen Minh Hieu', 'https://i.pravatar.cc/150?img=11', '1998-08-10', 'CUSTOMER', 2, 750,  b'1', NOW(6), NOW(6)),
('khachhang02', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'tuan.le@gmail.com',         '0912345678', 'Le Hoang Tuan',    'https://i.pravatar.cc/150?img=12', '1997-03-22', 'CUSTOMER', 3, 2150, b'1', NOW(6), NOW(6)),
('khachhang03', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'phuc.tran@gmail.com',       '0923456789', 'Tran Quoc Phuc',   NULL,                               '2000-12-05', 'CUSTOMER', 1, 120,  b'1', NOW(6), NOW(6)),
('khachhang04', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'an.pham@yahoo.com',         '0934567890', 'Pham Duc An',      'https://i.pravatar.cc/150?img=15', '1999-07-18', 'CUSTOMER', 1, 80,   b'1', NOW(6), NOW(6)),
('khachhang05', '$2a$10$rLfFK4LQmKRyQKUOSZj4E.nQMsXS0hcIbkgrDDziEy4gLoCZNTBGa', 'long.vo@gmail.com',         '0945678901', 'Vo Thanh Long',    'https://i.pravatar.cc/150?img=16', '1996-02-28', 'CUSTOMER', 4, 6200, b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 8. USER_ADDRESS (UserAddress.java)
--    AddressType enum: HOME, OFFICE, OTHER
-- -------------------------------------------------------------------------
INSERT INTO `user_address` (`user_id`, `address`, `address_type`, `province_id`, `province_name`, `district_id`, `district_name`, `ward_id`, `ward_name`, `is_default`, `created_at`, `updated_at`) VALUES
-- khachhang01 (user_id=3)
(3, '12 Nguyen Van Bao',  'HOME',   202, 'TP. Ho Chi Minh', 1461, 'Quan Go Vap',    '21301',  'Phuong 1',            b'1', NOW(6), NOW(6)),
(3, 'Tang 5 Toa nha ABC', 'OFFICE', 202, 'TP. Ho Chi Minh', 1462, 'Quan Binh Thanh','21608',  'Phuong 12',           b'0', NOW(6), NOW(6)),
-- khachhang02 (user_id=4)
(4, '58 Le Loi',          'HOME',   201, 'Ha Noi',          1486, 'Quan Dong Da',   '1A0421', 'Phuong Van Mieu',     b'1', NOW(6), NOW(6)),
-- khachhang03 (user_id=5)
(5, '99 Tran Phu',        'HOME',   203, 'Da Nang',         1526, 'Quan Hai Chau',  '40103',  'Phuong Hai Chau 1',   b'1', NOW(6), NOW(6)),
-- khachhang04 (user_id=6)
(6, '24 Hung Vuong',      'HOME',   202, 'TP. Ho Chi Minh', 1442, 'Quan 1',         '20101',  'Phuong Ben Nghe',     b'1', NOW(6), NOW(6)),
-- khachhang05 (user_id=7)
(7, '7 Pasteur',          'HOME',   202, 'TP. Ho Chi Minh', 1442, 'Quan 1',         '20102',  'Phuong Ben Thanh',    b'1', NOW(6), NOW(6)),
(7, '15 Ly Tu Trong',     'OFFICE', 202, 'TP. Ho Chi Minh', 1442, 'Quan 1',         '20104',  'Phuong Cau Ong Lang', b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 9. OTPS (Otp.java)
--    OtpPurpose enum: REGISTER, LOGIN, RESET_PASSWORD
--    Data lich su (is_used = 1), tru ban cuoi chua su dung
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
-- admin dang dang nhap (user_id=1)
('a1b2c3d4-0001-0001-0001-aabbccddeeff', 1, '2026-07-23 04:30:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang01 session cu da revoke - rotation (user_id=3)
('f1e2d3c4-0002-0002-0002-112233445566', 3, '2026-07-23 03:00:00.000000', '2026-07-16 10:00:00.000000', 'g1h2i3j4-0003-0003-0003-aabbccddeeff', '2026-07-16 03:00:00.000000', '2026-07-16 10:00:00.000000'),
-- khachhang01 session moi sau khi refresh (user_id=3)
('g1h2i3j4-0003-0003-0003-aabbccddeeff', 3, '2026-07-23 10:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang02 dang dang nhap (user_id=4)
('b2c3d4e5-0004-0004-0004-99aabbccddee', 4, '2026-07-23 05:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6)),
-- khachhang05 dang dang nhap (user_id=7)
('c3d4e5f6-0005-0005-0005-8899aabbccdd', 7, '2026-07-23 06:00:00.000000', NULL,                          NULL,                                   NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 11. PROMOTIONS (Promotion)
-- -------------------------------------------------------------------------
INSERT INTO `promotions` (`name`, `description`, `start_date`, `end_date`, `is_active`, `created_at`, `updated_at`) VALUES
('Chao He Ruc Ro',   'Giam gia cac san pham thoi trang mua he', '2026-06-01 00:00:00.000000', '2026-08-31 23:59:59.000000', b'1', NOW(6), NOW(6)),
('Flash Sale Tuan Le','Giam gia shock trong tuan',              '2026-07-10 00:00:00.000000', '2026-07-20 23:59:59.000000', b'1', NOW(6), NOW(6)),
('Black Friday',     'Dai tiec mua sam giam gia sieu sau',      '2026-11-20 00:00:00.000000', '2026-11-30 23:59:59.000000', b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 12. PRODUCTS (Product)
--     category_id theo thứ tự categories insert: Ao Thun=5, Ao So Mi=6, Ao Khoac=7, Quan Jeans=9, Giay Sneaker=13, Mu & Non=17
--     brand_id: Nike=1, Adidas=2, H&M=4, Routine=5, BILUXURY=6
-- -------------------------------------------------------------------------
INSERT INTO `products` (`category_id`, `brand_id`, `name`, `description`, `sold_quantity`, `created_at`, `updated_at`) VALUES
(5,  5, 'Ao Thun Nam Basic Routine',      'Ao thun cotton 100% thoang mat, form mau sac co ban de phoi do.',  150, NOW(6), NOW(6)), -- id=1
(13, 1, 'Giay The Thao Nike Air Force 1', 'Huyen thoai sneaker tu Nike, thiet ke co dien, nang dong.',        320, NOW(6), NOW(6)), -- id=2
(9,  4, 'Quan Jeans Nam Slimfit H&M',     'Quan jeans phom om vua phai, chat lieu co gian nhe, thoai mai.',   85,  NOW(6), NOW(6)), -- id=3
(6,  6, 'Ao So Mi Trang Cong So BILUXURY','Ao so mi nam cao cap, chong nhan, phu hop di lam, di tiec.',       210, NOW(6), NOW(6)), -- id=4
(17, 2, 'Mu Luoi Trai Adidas Thoi Trang', 'Mu the thao Adidas chat lieu kaki cao cap, tham hut mo hoi.',      45,  NOW(6), NOW(6)); -- id=5

-- -------------------------------------------------------------------------
-- 13. PRODUCT_VARIANTS (ProductVariant)
-- -------------------------------------------------------------------------
INSERT INTO `product_variants` (`product_id`, `name`, `price`, `discount_price`, `discount_rate`, `stock_total`, `stock_lock`, `created_at`, `updated_at`) VALUES
-- Ao thun Routine (product_id=1)
(1, 'Den - Size M',       250000.0000, 200000.0000, 20.00, 100, 5, NOW(6), NOW(6)), -- id=1
(1, 'Trang - Size L',     250000.0000, 200000.0000, 20.00, 100, 2, NOW(6), NOW(6)), -- id=2
-- Giay Nike (product_id=2)
(2, 'Trang - Size 42',   2500000.0000,       NULL,  NULL,   50, 0, NOW(6), NOW(6)), -- id=3
(2, 'Den - Size 43',     2500000.0000,       NULL,  NULL,   30, 1, NOW(6), NOW(6)), -- id=4
-- Quan Jeans H&M (product_id=3)
(3, 'Xanh Dam - Size 32',  650000.0000, 585000.0000, 10.00, 200, 0, NOW(6), NOW(6)), -- id=5
-- Ao so mi BILUXURY (product_id=4)
(4, 'Trang - Size 40',     450000.0000,       NULL,  NULL,  120,10, NOW(6), NOW(6)), -- id=6
-- Mu Adidas (product_id=5)
(5, 'Den - Freesize',      350000.0000, 300000.0000, 14.28,  80, 0, NOW(6), NOW(6)); -- id=7

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
--     Tags: Sale(1), New Arrival(2), Best Seller(3), Summer(5), Casual(7), Formal(8), Sport(10)
-- -------------------------------------------------------------------------
INSERT INTO `product_tags` (`product_id`, `tag_id`, `created_at`, `updated_at`) VALUES
(1, 1,  NOW(6), NOW(6)), -- Ao thun: Sale
(1, 5,  NOW(6), NOW(6)), -- Ao thun: Summer
(1, 7,  NOW(6), NOW(6)), -- Ao thun: Casual
(2, 3,  NOW(6), NOW(6)), -- Giay Nike: Best Seller
(2, 10, NOW(6), NOW(6)), -- Giay Nike: Sport
(3, 7,  NOW(6), NOW(6)), -- Quan Jeans: Casual
(4, 3,  NOW(6), NOW(6)), -- So mi: Best Seller
(4, 8,  NOW(6), NOW(6)), -- So mi: Formal
(5, 1,  NOW(6), NOW(6)), -- Mu: Sale
(5, 10, NOW(6), NOW(6)); -- Mu: Sport

-- -------------------------------------------------------------------------
-- 16. PROMOTION_PRODUCTS (PromotionProduct)
--     promotion_id: Chao He=1, Flash Sale=2
-- -------------------------------------------------------------------------
INSERT INTO `promotion_products` (`promotion_id`, `product_variant_id`, `discount_type`, `discount_value`, `created_at`, `updated_at`) VALUES
(1, 1, 'PERCENTAGE', 20.0000, NOW(6), NOW(6)), -- He: Ao thun Den M
(1, 2, 'PERCENTAGE', 20.0000, NOW(6), NOW(6)), -- He: Ao thun Trang L
(2, 7, 'FIXED',      50000.0000, NOW(6), NOW(6)); -- Flash: Mu Adidas (giam cung 50k)

-- -------------------------------------------------------------------------
-- 17. PRODUCT_REVIEWS (ProductReview)
-- -------------------------------------------------------------------------
INSERT INTO `product_reviews` (`product_id`, `user_id`, `rating`, `title`, `comment`, `created_at`, `updated_at`) VALUES
(2, 3, 5, 'Tuyet voi!',   'Giay dep, di rat em chan, giao hang nhanh.',          NOW(6), NOW(6)), -- khachhang01 danh gia Nike
(4, 4, 4, 'Chat vai dep', 'Ao dung form, mac rat mat, tuy nhien size hoi om.',   NOW(6), NOW(6)), -- khachhang02 danh gia So mi
(1, 5, 5, 'Ngon bo re',   'Gia hop ly, mac hang ngay rat ok.',                   NOW(6), NOW(6)); -- khachhang03 danh gia Ao thun

-- -------------------------------------------------------------------------
-- 18. CARTS (Cart) - 1 User chi co 1 Cart
-- -------------------------------------------------------------------------
INSERT INTO `carts` (`user_id`, `created_at`, `updated_at`) VALUES
(3, NOW(6), NOW(6)), -- Cart cua khachhang01 (id=1)
(4, NOW(6), NOW(6)), -- Cart cua khachhang02 (id=2)
(7, NOW(6), NOW(6)); -- Cart cua khachhang05 (id=3)

-- -------------------------------------------------------------------------
-- 19. CART_ITEMS (CartItem)
-- -------------------------------------------------------------------------
INSERT INTO `cart_items` (`cart_id`, `product_variant_id`, `quantity`, `created_at`, `updated_at`) VALUES
(1, 1, 2, NOW(6), NOW(6)), -- khachhang01: 2 Ao thun den
(1, 3, 1, NOW(6), NOW(6)), -- khachhang01: 1 Giay Nike trang
(2, 6, 3, NOW(6), NOW(6)), -- khachhang02: 3 So mi
(3, 5, 1, NOW(6), NOW(6)); -- khachhang05: 1 Quan jeans

-- -------------------------------------------------------------------------
-- 20. ORDERS (Order)
-- -------------------------------------------------------------------------
INSERT INTO `orders` (`user_id`, `user_address_id`, `coupon_id`, `coupon_code`, `first_name`, `last_name`, `phone_number`, `email`, `order_status`, `payment_method`, `is_paid`, `subtotal_original`, `product_discount_amount`, `rank_discount_amount`, `coupon_discount_amount`, `shipping_fee_original`, `shipping_fee_actual`, `tax_amount`, `total_order_amount`, `final_amount`, `notes`, `created_at`, `updated_at`) VALUES
-- Don hang 1: khachhang02 (user 4) - 2 Ao So Mi (2 x 450k = 900k)
-- Dang giao, Da thanh toan VN_PAY, Dung VIP15 giam 15%
(4, 3, 4, 'VIP15', 'Le Hoang', 'Tuan', '0912345678', 'tuan.le@gmail.com',
 'DELIVERING', 'VN_PAY', b'1',
 900000.0000, 0.0000, 45000.0000, 135000.0000, 30000.0000, 0.0000, 0.0000, 930000.0000, 750000.0000,
 'Giao gio hanh chinh giup minh', NOW(6), NOW(6)),

-- Don hang 2: khachhang05 (user 7) - 1 Giay Nike (2.5tr)
-- Da giao thanh cong, COD, Rank Diamond giam 20% = 500k
(7, 6, NULL, NULL, 'Vo Thanh', 'Long', '0945678901', 'long.vo@gmail.com',
 'DELIVERED', 'COD', b'1',
 2500000.0000, 0.0000, 500000.0000, 0.0000, 50000.0000, 50000.0000, 0.0000, 2550000.0000, 2050000.0000,
 'Goi hang can than nhe', NOW(6), NOW(6)),

-- Don hang 3: Khach vang lai (NULL user) - 1 Ao Thun den (variant 1 - gia goc 250k giam con 200k)
-- Cho xac nhan, COD
(NULL, NULL, NULL, NULL, 'Ngo Van', 'Khach', '0988777666', 'khach123@gmail.com',
 'PENDING', 'COD', b'0',
 250000.0000, 50000.0000, 0.0000, 0.0000, 25000.0000, 25000.0000, 0.0000, 275000.0000, 225000.0000,
 NULL, NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 21. ORDER_ITEMS (OrderItem)
-- -------------------------------------------------------------------------
INSERT INTO `order_items` (`order_id`, `product_variant_id`, `price`, `quantity`, `created_at`, `updated_at`) VALUES
(1, 6, 450000.0000, 2, NOW(6), NOW(6)),  -- Don 1: 2 Ao so mi 450k
(2, 3, 2500000.0000, 1, NOW(6), NOW(6)), -- Don 2: 1 Giay Nike 2.5tr
(3, 1, 200000.0000, 1, NOW(6), NOW(6));  -- Don 3: 1 Ao thun (gia da giam 200k)