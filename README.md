# ECOMMERCE FASHION FOR MEN

Đây là hệ thống Backend RESTful API cho nền tảng thương mại điện tử thời trang nam, được xây dựng theo kiến trúc phân lớp chuẩn (Controller – Service – Repository). Hệ thống xử lý toàn bộ luồng nghiệp vụ từ quản lý sản phẩm, đặt hàng, thanh toán đa cổng đến thông báo thời gian thực và tích hợp vận chuyển, phục vụ cả người dùng cuối lẫn quản trị viên.

---

## 1. CÔNG NGHỆ SỬ DỤNG

- **Ngôn ngữ & Runtime:** Java 21, Spring Boot 4.1.0

- **Web Framework:** Spring Web MVC (RESTful API)

- **Bảo mật:** Spring Security + JWT (jjwt 0.12.6) – xác thực stateless với Access Token & Refresh Token

- **Cơ sở dữ liệu:** MySQL / MariaDB, Spring Data JPA (Hibernate), Flyway (quản lý migration schema)

- **Tài liệu API:** SpringDoc OpenAPI (Swagger UI 2.8.4)

- **Tích hợp ngoài:**
  - **GHN (Giao Hàng Nhanh):** Tra cứu tỉnh/quận/phường, tính phí vận chuyển
  - **VNPay:** Cổng thanh toán trực tuyến (sandbox & production)
  - **MoMo:** Cổng thanh toán ví điện tử (sandbox & production)

- **Công cụ hỗ trợ:**
  - Lombok – giảm boilerplate code
  - ModelMapper 3.2.6 – ánh xạ DTO ↔ Entity
  - Jackson Databind + JSR310 – serialize/deserialize JSON (bao gồm kiểu dữ liệu thời gian)
  - Spring Mail (SMTP Gmail) – gửi email OTP xác thực
  - Maven – quản lý dependency và build
  - Git – quản lý mã nguồn và phiên bản

---

## 2. CÁC PHÂN HỆ NGHIỆP VỤ CHÍNH

- **Quản lý sản phẩm (Product Management):** CRUD sản phẩm, biến thể sản phẩm (ProductVariant), hình ảnh (ProductImages), nhãn (Tag), thương hiệu (Brand), danh mục (Category) và thuộc tính tùy chỉnh (Attribute/AttributeValue)

- **Giỏ hàng & Đặt hàng (Cart & Order):** Quản lý giỏ hàng theo phiên người dùng, tạo đơn hàng, theo dõi trạng thái đơn theo từng bước (OrderStatusHistory), tự động hủy đơn hết hạn thanh toán (OrderAutoExpireSchedulerService)

- **Thanh toán đa cổng (Payment):** Tích hợp VNPay và MoMo với cơ chế IPN callback xác minh chữ ký, timeout tự động 15 phút, và đối soát giao dịch

- **Mã giảm giá & Khuyến mãi (Coupon & Promotion):** Tạo và áp dụng coupon (giảm theo phần trăm hoặc số tiền cố định), quản lý chương trình khuyến mãi theo sản phẩm, tự động kích hoạt/hết hạn qua PromotionSchedulerService

- **Hệ thống hạng thành viên & Tích điểm (Rank & Loyalty):** Phân hạng khách hàng (Rank), tích lũy điểm theo đơn hàng (1.000 VNĐ = 1 điểm), đổi điểm giảm giá

- **Vận chuyển (Shipment):** Tích hợp API GHN để tra địa chỉ và tính phí ship theo địa chỉ giao hàng thực tế

- **Xác thực & Phân quyền (Auth & Security):** Đăng ký/đăng nhập bằng email, xác minh OTP qua Gmail, JWT stateless với cơ chế Refresh Token Session, phân quyền ADMIN/USER

- **Thông báo (Notification):** Gửi thông báo nội hệ thống theo sự kiện đơn hàng (đặt hàng, xác nhận, giao hàng, hoàn thành, hủy)

- **Đánh giá sản phẩm (Review):** Khách hàng gửi đánh giá sau khi đơn hoàn thành, quản trị viên duyệt/xóa bình luận

- **Banner & Giao diện trang chủ (Banner):** Quản lý banner quảng cáo hiển thị trên storefront

- **Quản trị người dùng (Admin User):** Xem, phân quyền, khoá/mở tài khoản, xem lịch sử đơn hàng theo người dùng

---

## 3. CẤU TRÚC THƯ MỤC SOURCE CODE

```
src/
└── main/
    ├── java/com/example/ecommerce_fashionformen/
    │   ├── config/              # Cấu hình Spring Security, VNPay, MoMo, GHN, ModelMapper
    │   ├── controllers/         # REST Controller cho từng nghiệp vụ (24 controller)
    │   ├── domain/
    │   │   ├── entity/          # 27 JPA Entity (Product, Order, User, Coupon, Shipment...)
    │   │   ├── enums/           # Các kiểu liệt kê: OrderStatus, PaymentMethod, UserRole, RankName...
    │   │   ├── AuditableEntity  # Base entity ghi nhận createdAt / updatedAt tự động
    │   │   └── BaseEntity       # Base entity chứa trường ID chung
    │   ├── dto/                 # Request/Response DTO tách biệt khỏi Entity
    │   ├── repository/          # Spring Data JPA Repository interfaces
    │   ├── security/            # JWT Filter, TokenProvider, UserDetailsService, EntryPoint
    │   └── services/
    │       ├── *.java           # Interface định nghĩa contract nghiệp vụ
    │       └── Impl/            # 27 Service Implementation (bao gồm 2 Scheduler tự động)
    └── resources/
        ├── application.yaml        # Cấu hình chính (profile, JPA, mail, JWT, payment, GHN)
        ├── application-dev.yaml    # Cấu hình môi trường phát triển
        ├── application-prod.yaml   # Cấu hình môi trường production
        └── db/migration/
            ├── V1__init_schema.sql # Khởi tạo toàn bộ schema cơ sở dữ liệu
            └── V2__data.sql        # Dữ liệu mẫu ban đầu (seed data)
```

---

## 4. ĐIỂM MẠNH CỦA DỰ ÁN

- **Kiến trúc phân lớp rõ ràng:** Tách biệt Controller – Service Interface – Service Impl – Repository giúp dễ bảo trì, mở rộng và viết unit test độc lập từng lớp

- **Bảo mật JWT Stateless:** Không lưu session phía server, hỗ trợ Access Token ngắn hạn (8 giờ) + Refresh Token dài hạn (7 ngày) với cơ chế thu hồi token linh hoạt

- **Tích hợp thanh toán thực tế:** Hỗ trợ song song VNPay và MoMo với xác minh IPN callback bằng chữ ký HMAC, an toàn trước tấn công giả mạo

- **Tự động hóa nghiệp vụ:** Hai Scheduler tự động vận hành nền – `OrderAutoExpireSchedulerService` hủy đơn quá hạn và `PromotionSchedulerService` cập nhật trạng thái khuyến mãi đúng giờ

- **Quản lý schema với Flyway:** Lịch sử migration SQL được version hóa, đảm bảo đồng nhất schema giữa các môi trường (dev/prod) và an toàn khi triển khai

- **Tích hợp vận chuyển GHN:** Tra cứu địa chỉ và tính phí vận chuyển động theo địa chỉ thực tế, không hardcode phí ship

- **Xác thực OTP qua Email:** Luồng đăng ký và đặt lại mật khẩu bảo mật hai bước qua Gmail SMTP, OTP có TTL giới hạn

- **Tài liệu API tự động:** Swagger UI tích hợp sẵn qua SpringDoc OpenAPI, hỗ trợ test API trực tiếp không cần Postman