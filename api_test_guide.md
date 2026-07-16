# 🧪 Hướng dẫn Test API Nhanh — FashionForMen

> **Base URL:** `http://localhost:8080/api/v1`  
> **Headers chung (cho các API cần đăng nhập):** `Authorization: Bearer <accessToken>`

---

## 🔐 1. Luồng Đăng nhập & Đăng ký

### Đăng ký tài khoản mới (Happy Path)
* **POST** `/auth/register`
* **Body:**
```json
{
  "username": "testuser01",
  "password": "123456",
  "email": "testuser01@gmail.com",
  "phone": "0978123456",
  "fullName": "Nguyen Van Test",
  "dateOfBirth": "2000-05-15"
}
```

### Đăng nhập (Lấy Token)
* **POST** `/auth/login`
* **Body:**
```json
{
  "username": "khachhang01",
  "password": "123456"
}
```
> *(Copy `accessToken` từ kết quả trả về bỏ vào Header `Authorization` cho các API dưới)*

### Làm mới Token (Refresh Token)
* **POST** `/auth/refresh`
* **Body:**
```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

---

## 👤 2. Địa chỉ người dùng (User Address)

### Lấy danh sách địa chỉ của user (khachhang01 — id=3)
* **GET** `/user-addresses/user/3`

### Tạo địa chỉ mới
* **POST** `/user-addresses`
* **Body:**
```json
{
  "userId": 3,
  "address": "45 Nguyen Trai, Phuong 3",
  "addressType": "HOME",
  "provinceId": 202,
  "provinceName": "TP. Ho Chi Minh",
  "districtId": 1449,
  "districtName": "Quan 3",
  "wardId": "Q300003",
  "wardName": "Phuong 3",
  "isDefault": false
}
```

### Cập nhật địa chỉ (id=1)
* **PUT** `/user-addresses/1`
* **Body:**
```json
{
  "userId": 3,
  "address": "12 Nguyen Van Bao - Cap nhat",
  "addressType": "HOME",
  "provinceId": 202,
  "provinceName": "TP. Ho Chi Minh",
  "districtId": 1454,
  "districtName": "Quan Go Vap",
  "wardId": "GV00001",
  "wardName": "Phuong 1",
  "isDefault": true
}
```

### Xóa địa chỉ (id=1)
* **DELETE** `/user-addresses/1`

---

## ⚙️ 3. Quản lý Danh mục khác (Ranks, Brands, Categories, Tags, Coupons, Banners)

### Hạng thành viên (Ranks)
* **GET** `/ranks` (Lấy tất cả hạng)
* **GET** `/ranks/1` (Lấy hạng theo ID)
* **PUT** `/ranks/1` (Cập nhật hạng)
  ```json
  {
    "rankName": "BRONZE",
    "point": 0,
    "rankDiscount": 2.5
  }
  ```

### Thương hiệu (Brands)
* **GET** `/brands` (Lấy tất cả)
* **GET** `/brands/active` (Lấy thương hiệu hoạt động)
* **POST** `/brands` (Tạo thương hiệu)
  ```json
  {
    "name": "Uniqlo",
    "description": "Thuong hieu Nhat Ban",
    "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/9/92/UNIQLO_logo.svg",
    "isActive": true
  }
  ```

### Danh mục sản phẩm (Categories)
* **GET** `/categories` (Lấy tất cả)
* **GET** `/categories/roots` (Lấy danh mục gốc)
* **POST** `/categories` (Tạo danh mục)
  ```json
  {
    "name": "Do Thể Thao Nam",
    "parentId": null
  }
  ```

### Thẻ tag (Tags)
* **GET** `/tags` (Lấy tất cả)
* **POST** `/tags` (Tạo tag)
  ```json
  {
    "name": "Eco-Friendly",
    "description": "Than thien moi truong"
  }
  ```

### Mã giảm giá (Coupons)
* **GET** `/coupons` (Lấy tất cả)
* **GET** `/coupons/code/SUMMER20` (Tìm theo code)
* **POST** `/coupons` (Tạo coupon)
  ```json
  {
    "code": "NEWYEAR27",
    "name": "Khuyen mai Nam Moi",
    "discountRate": 20.0,
    "maxDiscountAmount": 50000.0,
    "minOrderValue": 200000.0,
    "startDate": "2027-01-01T00:00:00",
    "endDate": "2027-01-07T23:59:59",
    "usageLimit": 100,
    "isActive": true
  }
  ```

### Banners
* **GET** `/banners` (Lấy tất cả)
* **GET** `/banners/active` (Lấy banner đang chạy)
* **POST** `/banners` (Tạo banner)
  ```json
  {
    "title": "Sale Tet 2027",
    "imageUrl": "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=1200",
    "linkUrl": "/san-pham?tag=sale",
    "displayOrder": 1,
    "isActive": true
  }
  ```

---

## 🔑 Danh sách tài khoản mẫu có sẵn (Mật khẩu: `123456`)

* **Admin:** `admin` (Email: `admin@fashionformen.vn`)
* **Staff:** `staff_nam` (Email: `nam.staff@fashionformen.vn`)
* **Customer 01:** `khachhang01` (Email: `hieu.nguyen@gmail.com`)
* **Customer 02:** `khachhang02` (Email: `tuan.le@gmail.com`)
