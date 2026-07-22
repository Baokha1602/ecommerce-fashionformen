package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.*;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.repository.*;
import com.example.ecommerce_fashionformen.dto.order.OrderItemRequest;
import com.example.ecommerce_fashionformen.services.GhnService;
import com.example.ecommerce_fashionformen.services.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final UserAddressRepository userAddressRepository;
    private final ShipmentRepository shipmentRepository;
    private final GhnService ghnService;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;



    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        // Lấy địa chỉ giao hàng để tính phí GHN
        UserAddress userAddress = userAddressRepository.findById(request.getUserAddressId())
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại"));

        Order order = mapper.map(request, Order.class);
        order.setUser(user);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;

        // Lưu order trước để có ID cho OrderItem
        order = orderRepository.save(order);

        int totalQuantity = 0;
        if (request.getOrderItems() != null) {
            for (OrderItemRequest itemReq : request.getOrderItems()) {
                OrderItem orderItem = mapper.map(itemReq, OrderItem.class);
                orderItem.setOrder(order);

                ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                        .orElseThrow(() -> new NotFoundException("Biến thể sản phẩm không tồn tại"));

                BigDecimal itemPrice = variant.getPrice();
                BigDecimal itemDiscountPrice = variant.getDiscountPrice() != null
                        ? variant.getDiscountPrice() : itemPrice;

                orderItem.setPrice(itemDiscountPrice);

                BigDecimal qty = new BigDecimal(itemReq.getQuantity());
                subtotal = subtotal.add(itemPrice.multiply(qty));
                productDiscount = productDiscount.add(itemPrice.subtract(itemDiscountPrice).multiply(qty));
                totalQuantity += itemReq.getQuantity();

                orderItemRepository.save(orderItem);
            }
        }

        order.setSubtotalOriginal(subtotal);
        order.setProductDiscountAmount(productDiscount);

        // ── Coupon / Rank discount ────────────────────────────────────────────
        BigDecimal rankDiscountAmount = BigDecimal.ZERO;
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(request.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (Boolean.TRUE.equals(coupon.getIsActive())
                        && LocalDateTime.now().isAfter(coupon.getStartDate())
                        && LocalDateTime.now().isBefore(coupon.getEndDate())
                        && subtotal.subtract(productDiscount).compareTo(coupon.getMinOrderValue()) >= 0) {

                    order.setCouponId(coupon.getId());
                    BigDecimal discountableAmount = subtotal.subtract(productDiscount);
                    couponDiscountAmount = discountableAmount
                            .multiply(coupon.getDiscountRate())
                            .divide(new BigDecimal("100"));

                    if (coupon.getMaxDiscountAmount() != null
                            && couponDiscountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                        couponDiscountAmount = coupon.getMaxDiscountAmount();
                    }
                }
            }
        } else {
            Rank rank = user.getRank();
            if (rank != null && rank.getRankDiscount() != null) {
                BigDecimal discountableAmount = subtotal.subtract(productDiscount);
                rankDiscountAmount = discountableAmount
                        .multiply(rank.getRankDiscount())
                        .divide(new BigDecimal("100"));
            }
        }

        order.setCouponDiscountAmount(couponDiscountAmount);
        order.setRankDiscountAmount(rankDiscountAmount);

        // ── Tính phí vận chuyển từ GHN (có fallback 30,000 nếu lỗi) ─────────
        BigDecimal shippingFee = ghnService.calculateShippingFee(
                totalQuantity,
                userAddress.getDistrictId().intValue(),
                userAddress.getWardId()
        );
        order.setShippingFeeOriginal(shippingFee);
        order.setShippingFeeActual(shippingFee);

        // ── Tổng tiền ─────────────────────────────────────────────────────────
        BigDecimal totalOrderAmount = subtotal.add(shippingFee);
        order.setTotalOrderAmount(totalOrderAmount);

        BigDecimal finalAmount = subtotal
                .subtract(productDiscount)
                .subtract(couponDiscountAmount)
                .subtract(rankDiscountAmount)
                .add(shippingFee);
        order.setFinalAmount(finalAmount);

        order = orderRepository.save(order);
        return mapper.map(order, OrderResponse.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin: Giao hàng → tạo đơn GHN + lưu Shipment
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Chỉ có thể giao hàng khi đơn ở trạng thái PROCESSING. " +
                    "Trạng thái hiện tại: " + order.getOrderStatus());
        }

        UserAddress address = userAddressRepository.findById(order.getUserAddressId())
                .orElseThrow(() -> new NotFoundException("Địa chỉ giao hàng không tồn tại"));

        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);

        // Gọi GHN tạo đơn vận chuyển
        GhnCreateOrderResponse ghnResponse = ghnService.createShippingOrder(order, address, orderItems);

        // Lưu thông tin vận đơn vào bảng shipments
        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setGhnOrderCode(ghnResponse.getData().getOrderCode());
        shipment.setExpectedDeliveryTime(ghnResponse.getData().getExpectedDeliveryTime());

        // COD: 0 nếu đã thanh toán, ngược lại = finalAmount
        BigDecimal codAmount = Boolean.TRUE.equals(order.getIsPaid())
                ? BigDecimal.ZERO
                : order.getFinalAmount();
        shipment.setCodAmount(codAmount);

        int totalQuantity = orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
        shipment.setTotalWeight(totalQuantity * 300); // 300g/sản phẩm

        if (ghnResponse.getData().getTotalFee() != null) {
            shipment.setTotalFee(BigDecimal.valueOf(ghnResponse.getData().getTotalFee()));
        }

        // Lưu raw JSON response để audit/debug
        try {
            shipment.setGhnRawResponse(objectMapper.writeValueAsString(ghnResponse));
        } catch (JsonProcessingException e) {
            log.warn("[GHN] Không thể serialize raw response: {}", e.getMessage());
        }

        shipmentRepository.save(shipment);
        log.info("[GHN] Đã lưu shipment → orderId={}, ghnCode={}",
                orderId, ghnResponse.getData().getOrderCode());

        // Cập nhật trạng thái đơn hàng → DELIVERING
        order.setOrderStatus(OrderStatus.DELIVERING);
        order = orderRepository.save(order);

        return mapper.map(order, OrderResponse.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin: Cập nhật trạng thái đơn hàng thủ công
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));
        order.setOrderStatus(status);
        return mapper.map(orderRepository.save(order), OrderResponse.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public List<OrderResponse> getOrderHistory(Long userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                .map(o -> mapper.map(o, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));
        return mapper.map(order, OrderResponse.class);
    }
}
