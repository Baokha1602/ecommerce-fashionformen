


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
('Nike',     'Thuong hieu the thao hang dau the gioi tu My.',          'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg',         b'1', NOW(6), NOW(6)),
('Adidas',   'Thuong hieu the thao noi tieng toan cau tu Duc.',        'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg',        b'1', NOW(6), NOW(6)),
('Zara',     'Thoi trang nhanh cao cap tu Tay Ban Nha.',               'https://upload.wikimedia.org/wikipedia/commons/f/fd/Zara_Logo.svg',           b'1', NOW(6), NOW(6)),
('H&M',      'Thoi trang binh dan pho bien tu Thuy Dien.',             'https://upload.wikimedia.org/wikipedia/commons/5/53/H%26M-Logo.svg',          b'1', NOW(6), NOW(6)),
('Routine',  'Thuong hieu thoi trang nam noi dia Viet Nam hien dai.',  'https://routineclothes.com/wp-content/uploads/2023/01/routine-logo-dark.png', b'1', NOW(6), NOW(6)),
('BILUXURY', 'Thuong hieu suit & vest cao cap danh rieng cho nam.',    'https://biluxury.vn/wp-content/uploads/2022/07/logo-biluxury.png',            b'1', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 3. CATEGORIES (Category.java) - Phan cap cha/con
-- -------------------------------------------------------------------------
-- Danh muc cha
INSERT INTO `categories` (`name`, `parent_id`, `created_at`, `updated_at`) VALUES
('Ao',         NULL, NOW(6), NOW(6)),
('Quan',       NULL, NOW(6), NOW(6)),
('Giay & Dep', NULL, NOW(6), NOW(6)),
('Phu kien',   NULL, NOW(6), NOW(6));

-- Danh muc con (parent_id = id cua danh muc cha tuong ung)
INSERT INTO `categories` (`name`, `parent_id`, `created_at`, `updated_at`) VALUES
('Ao Thun',      1, NOW(6), NOW(6)),
('Ao So Mi',     1, NOW(6), NOW(6)),
('Ao Khoac',     1, NOW(6), NOW(6)),
('Ao Hoodie',    1, NOW(6), NOW(6)),
('Quan Jeans',   2, NOW(6), NOW(6)),
('Quan Short',   2, NOW(6), NOW(6)),
('Quan Tay',     2, NOW(6), NOW(6)),
('Quan Jogger',  2, NOW(6), NOW(6)),
('Giay Sneaker', 3, NOW(6), NOW(6)),
('Giay Tay',     3, NOW(6), NOW(6)),
('Dep Sandal',   3, NOW(6), NOW(6)),
('That Lung',    4, NOW(6), NOW(6)),
('Mu & Non',     4, NOW(6), NOW(6)),
('Tui & Ba Lo',  4, NOW(6), NOW(6));

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
('Summer Sale 2026 - Giam den 50%',    'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200', '/san-pham?tag=sale',        1, b'1', NOW(6), NOW(6)),
('Bo Suu Tap Moi - New Arrival',       'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200', '/san-pham?tag=new-arrival', 2, b'1', NOW(6), NOW(6)),
('Hang Moi Ve Moi Tuan',               'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=1200', '/san-pham',                 3, b'1', NOW(6), NOW(6)),
('Phong Cach Nam Tinh - BILUXURY Suit','https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1200', '/thuong-hieu/biluxury',      4, b'1', NOW(6), NOW(6)),
('Flash Sale Cuoi Tuan',               'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200', '/san-pham?tag=sale',        5, b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 6. COUPONS (Coupon.java)
-- -------------------------------------------------------------------------
INSERT INTO `coupons` (`code`, `name`, `discount_rate`, `max_discount_amount`, `min_order_value`, `start_date`, `end_date`, `usage_limit`, `is_active`, `created_at`, `updated_at`) VALUES
('WELCOME10',  'Mung khach hang moi - Giam 10%',       10.00,  50000.0000, 100000.0000, '2026-01-01 00:00:00.000000', '2026-12-31 23:59:59.000000', 1000, b'1', NOW(6), NOW(6)),
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
('admin',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@fashionformen.vn',     '0900000001', 'Nguyen Quan Tri',  NULL,                               '1990-01-15', 'ADMIN',    4, 9999, b'1', NOW(6), NOW(6)),
-- Staff (id=2)
('staff_nam',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'nam.staff@fashionformen.vn', '0900000002', 'Tran Van Nam',     NULL,                               '1995-05-20', 'STAFF',    1, 0,    b'1', NOW(6), NOW(6)),
-- Customers
('khachhang01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'hieu.nguyen@gmail.com',     '0901234567', 'Nguyen Minh Hieu', 'https://i.pravatar.cc/150?img=11', '1998-08-10', 'CUSTOMER', 2, 750,  b'1', NOW(6), NOW(6)),
('khachhang02', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'tuan.le@gmail.com',         '0912345678', 'Le Hoang Tuan',    'https://i.pravatar.cc/150?img=12', '1997-03-22', 'CUSTOMER', 3, 2150, b'1', NOW(6), NOW(6)),
('khachhang03', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'phuc.tran@gmail.com',       '0923456789', 'Tran Quoc Phuc',   NULL,                               '2000-12-05', 'CUSTOMER', 1, 120,  b'1', NOW(6), NOW(6)),
('khachhang04', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'an.pham@yahoo.com',         '0934567890', 'Pham Duc An',      'https://i.pravatar.cc/150?img=15', '1999-07-18', 'CUSTOMER', 1, 80,   b'1', NOW(6), NOW(6)),
('khachhang05', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'long.vo@gmail.com',         '0945678901', 'Vo Thanh Long',    'https://i.pravatar.cc/150?img=16', '1996-02-28', 'CUSTOMER', 4, 6200, b'1', NOW(6), NOW(6));


-- -------------------------------------------------------------------------
-- 8. USER_ADDRESS (UserAddress.java)
--    AddressType enum: HOME, OFFICE, OTHER
-- -------------------------------------------------------------------------
INSERT INTO `user_address` (`user_id`, `address`, `address_type`, `province_id`, `province_name`, `district_id`, `district_name`, `ward_id`, `ward_name`, `is_default`, `created_at`, `updated_at`) VALUES
-- khachhang01 (user_id=3)
(3, '12 Nguyen Van Bao',   'HOME',   202, 'TP. Ho Chi Minh', 1454, 'Quan Go Vap',    'GV00001', 'Phuong 1',           b'1', NOW(6), NOW(6)),
(3, 'Tang 5 Toa nha ABC',  'OFFICE', 202, 'TP. Ho Chi Minh', 1442, 'Quan Binh Thanh','BT00010', 'Phuong 10',          b'0', NOW(6), NOW(6)),
-- khachhang02 (user_id=4)
(4, '58 Le Loi',            'HOME',   201, 'Ha Noi',          1490, 'Quan Dong Da',   'DD00005', 'Phuong Van Mieu',    b'1', NOW(6), NOW(6)),
-- khachhang03 (user_id=5)
(5, '99 Tran Phu',          'HOME',   203, 'Da Nang',         1565, 'Quan Hai Chau',  'HC00002', 'Phuong Hai Chau 1', b'1', NOW(6), NOW(6)),
-- khachhang04 (user_id=6)
(6, '24 Hung Vuong',        'HOME',   202, 'TP. Ho Chi Minh', 1444, 'Quan 1',         'Q100001', 'Phuong Ben Nghe',   b'1', NOW(6), NOW(6)),
-- khachhang05 (user_id=7)
(7, '7 Pasteur',            'HOME',   202, 'TP. Ho Chi Minh', 1444, 'Quan 1',         'Q100003', 'Phuong Ben Thanh',  b'1', NOW(6), NOW(6)),
(7, '15 Ly Tu Trong',       'OFFICE', 202, 'TP. Ho Chi Minh', 1444, 'Quan 1',         'Q100004', 'Phuong Cau Ong Lang',b'0', NOW(6), NOW(6));

-- -------------------------------------------------------------------------
-- 9. OTPS (Otp.java)
--    OtpPurpose enum: REGISTER, LOGIN, RESET_PASSWORD
--    Day la data lich su (is_used = 1), tru ban cuoi chua su dung
-- -------------------------------------------------------------------------
INSERT INTO `otps` (`otp_code`, `email`, `expires_at`, `is_used`, `purpose`, `failed_otp_attempts`, `reset_password_token`, `created_at`, `updated_at`) VALUES
('472831', 'hieu.nguyen@gmail.com', '2026-07-10 10:05:00.000000', b'1', 'REGISTER',       0, NULL,                        '2026-07-10 10:00:00.000000', '2026-07-10 10:00:00.000000'),
('918273', 'tuan.le@gmail.com',     '2026-07-11 09:05:00.000000', b'1', 'LOGIN',           0, NULL,                        '2026-07-11 09:00:00.000000', '2026-07-11 09:00:00.000000'),
('334521', 'phuc.tran@gmail.com',   '2026-07-12 15:05:00.000000', b'1', 'RESET_PASSWORD',  1, 'reset-token-phuc-abc123xyz','2026-07-12 15:00:00.000000', '2026-07-12 15:00:00.000000'),
('654892', 'an.pham@yahoo.com',     '2026-07-15 08:05:00.000000', b'1', 'REGISTER',        0, NULL,                        '2026-07-15 08:00:00.000000', '2026-07-15 08:00:00.000000'),
('123456', 'long.vo@gmail.com',     '2026-07-16 11:35:00.000000', b'0', 'LOGIN',            0, NULL,                        '2026-07-16 11:30:00.000000', '2026-07-16 11:30:00.000000');

-- -------------------------------------------------------------------------
-- 10. REFRESH_TOKEN_SESSIONS (RefreshTokenSession.java)
--     jti (JWT Token ID) la UUID ngan gon de de doc
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
