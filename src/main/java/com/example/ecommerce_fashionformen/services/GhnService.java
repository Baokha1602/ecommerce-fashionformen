package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.entity.OrderItem;
import com.example.ecommerce_fashionformen.domain.entity.UserAddress;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderResponse;

import java.math.BigDecimal;
import java.util.List;

public interface GhnService {

    /**
     * Tính phí vận chuyển từ GHN API dựa trên tổng số lượng sản phẩm
     * và địa chỉ người nhận.
     *
     * @param totalQuantity Tổng số lượng sản phẩm trong đơn hàng
     * @param districtId    ID quận/huyện người nhận (GHN district_id)
     * @param wardCode      Mã phường/xã người nhận (GHN ward_code)
     * @return Phí vận chuyển (VNĐ). Trả về 30,000 nếu GHN không phản hồi.
     */
    BigDecimal calculateShippingFee(int totalQuantity, int districtId, String wardCode);

    /**
     * Tạo đơn vận chuyển GHN khi Admin xác nhận giao hàng.
     *
     * @param order      Đơn hàng cần giao
     * @param address    Địa chỉ người nhận
     * @param orderItems Danh sách sản phẩm trong đơn
     * @return Response từ GHN chứa order_code và thời gian dự kiến giao
     */
    GhnCreateOrderResponse createShippingOrder(Order order, UserAddress address, List<OrderItem> orderItems);
}
