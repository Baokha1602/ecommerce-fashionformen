package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.*;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.dto.order.OrderAdminResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderStatsResponse;
import com.example.ecommerce_fashionformen.dto.order.OrderStatusHistoryResponse;
import com.example.ecommerce_fashionformen.repository.*;
import com.example.ecommerce_fashionformen.dto.ghn.GhnCreateOrderResponse;
import com.example.ecommerce_fashionformen.services.AdminOrderService;
import com.example.ecommerce_fashionformen.services.GhnService;
import com.example.ecommerce_fashionformen.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final NotificationService notificationService;
    private final GhnService ghnService;
    private final ShipmentRepository shipmentRepository;
    private final UserAddressRepository userAddressRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    @Value("${app.order.point-per-amount:1000}")
    private int pointPerAmount;

    /**
     * State machine cho chuyển trạng thái đơn hàng.
     * Key = trạng thái hiện tại, Value = set trạng thái có thể chuyển tới.
     */
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, Set.of(OrderStatus.DELIVERING, OrderStatus.CANCELLED),
            OrderStatus.DELIVERING, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus orderStatus, PaymentMethod paymentMethod,
                                            LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderStatus != null) {
                predicates.add(cb.equal(root.get("orderStatus"), orderStatus));
            }
            if (paymentMethod != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), paymentMethod));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec, pageable)
                .map(o -> mapper.map(o, OrderResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderAdminResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        OrderAdminResponse response = mapper.map(order, OrderAdminResponse.class);

        // Thêm status history
        List<OrderStatusHistory> histories = orderStatusHistoryRepository
                .findByOrderIdOrderByChangedAtAsc(orderId);
        List<OrderStatusHistoryResponse> historyResponses = histories.stream()
                .map(h -> mapper.map(h, OrderStatusHistoryResponse.class))
                .collect(Collectors.toList());
        response.setStatusHistory(historyResponses);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, String note, String adminUsername) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        OrderStatus currentStatus = order.getOrderStatus();

        // Validate state machine transition
        Set<OrderStatus> allowedTransitions = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedTransitions.contains(newStatus)) {
            throw new BadRequestException(
                    "Không thể chuyển từ trạng thái " + currentStatus.name()
                            + " sang " + newStatus.name()
                            + ". Các trạng thái hợp lệ: " + allowedTransitions);
        }

        order.setOrderStatus(newStatus);

        // Tạo đơn GHN khi chuyển sang DELIVERING
        if (newStatus == OrderStatus.DELIVERING) {
            handleDelivering(order);
        }

        // Xử lý logic đặc biệt khi chuyển sang DELIVERED
        if (newStatus == OrderStatus.DELIVERED) {
            handleDelivered(order);
        }

        // Xử lý logic đặc biệt khi admin CANCELLED
        if (newStatus == OrderStatus.CANCELLED) {
            handleCancelled(order);
        }

        orderRepository.save(order);

        // Lưu status history
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .changedBy(adminUsername)
                .changedAt(LocalDateTime.now())
                .note(note)
                .build();
        orderStatusHistoryRepository.save(statusHistory);

        // Thông báo cho ADMIN/STAFF khi admin/staff hủy đơn (fire-and-forget)
        if (newStatus == OrderStatus.CANCELLED) {
            try {
                notificationService.notifyOrderCancelled(
                        orderId, adminUsername + " (Quản trị)");
            } catch (Exception e) {
                log.warn("[NOTIFICATION] Không thể gửi thông báo admin hủy đơn #{}: {}",
                        orderId, e.getMessage());
            }
        }

        return mapper.map(order, OrderResponse.class);
    }

    /**
     * Khi admin chuyển sang DELIVERING:
     * - Lấy địa chỉ giao hàng từ userAddressId của đơn hàng
     * - Gọi GHN API tạo đơn vận chuyển
     * - Lưu thông tin vận đơn vào bảng shipments
     * - Lỗi GHN không block việc cập nhật trạng thái (chỉ log warning)
     */
    private void handleDelivering(Order order) {
        // Kiểm tra đã có vận đơn GHN cho đơn này chưa (tránh tạo duplicate)
        if (shipmentRepository.findByOrderId(order.getId()).isPresent()) {
            log.warn("[GHN] Đơn hàng #{} đã có vận đơn GHN, bỏ qua.", order.getId());
            return;
        }

        UserAddress address = null;
        if (order.getUserAddressId() != null) {
            address = userAddressRepository.findById(order.getUserAddressId()).orElse(null);
        }

        if (address == null) {
            log.warn("[GHN] Đơn hàng #{} không có địa chỉ giao hàng hợp lệ, bỏ qua tạo đơn GHN.",
                    order.getId());
            return;
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        if (items.isEmpty()) {
            log.warn("[GHN] Đơn hàng #{} không có sản phẩm, bỏ qua tạo đơn GHN.", order.getId());
            return;
        }

        try {
            GhnCreateOrderResponse ghnResponse = ghnService.createShippingOrder(order, address, items);
            GhnCreateOrderResponse.GhnCreateOrderData data = ghnResponse.getData();

            // Tính tổng khối lượng
            int totalQty = items.stream().mapToInt(OrderItem::getQuantity).sum();
            int totalWeight = totalQty * 300; // 300g/sản phẩm (khớp GhnServiceImpl)

            // Lưu vận đơn vào DB
            Shipment shipment = new Shipment();
            shipment.setOrderId(order.getId());
            shipment.setGhnOrderCode(data.getOrderCode());
            shipment.setExpectedDeliveryTime(data.getExpectedDeliveryTime());
            shipment.setTotalWeight(totalWeight);
            shipment.setCodAmount(Boolean.TRUE.equals(order.getIsPaid())
                    ? java.math.BigDecimal.ZERO
                    : order.getFinalAmount());
            if (data.getTotalFee() != null) {
                shipment.setTotalFee(java.math.BigDecimal.valueOf(data.getTotalFee()));
            }
            // Lưu raw JSON response để debug/audit
            try {
                shipment.setGhnRawResponse(objectMapper.writeValueAsString(ghnResponse));
            } catch (Exception ignored) { /* không cần thiết, bỏ qua */ }

            shipmentRepository.save(shipment);

            log.info("[GHN] Tạo vận đơn thành công cho đơn #{} → ghnOrderCode={}, expectedDelivery={}",
                    order.getId(), data.getOrderCode(), data.getExpectedDeliveryTime());

        } catch (Exception e) {
            // Không throw exception — GHN lỗi không nên block việc đổi trạng thái
            log.error("[GHN] Tạo vận đơn thất bại cho đơn #{}: {}", order.getId(), e.getMessage(), e);
        }
    }

    /**
     * Khi đơn DELIVERED:
     * - Trừ thật stockTotal (hàng đã giao)
     * - Cộng điểm tích lũy cho user (finalAmount / pointPerAmount)
     */
    private void handleDelivered(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem item : items) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getProductVariantId())
                    .orElse(null);
            if (variant != null) {
                // Trừ stockTotal (hàng đã giao thực sự)
                variant.setStockTotal(Math.max(0, variant.getStockTotal() - item.getQuantity()));
                // Nhả stockLock (vì hàng đã giao, không cần giữ chỗ nữa)
                variant.setStockLock(Math.max(0, variant.getStockLock() - item.getQuantity()));
                productVariantRepository.save(variant);
            }
        }

        // Cộng điểm tích lũy cho user
        User user = order.getUser();
        if (user != null && pointPerAmount > 0) {
            int earnedPoints = order.getFinalAmount().intValue() / pointPerAmount;
            if (earnedPoints > 0) {
                user.setCurrentPoint(user.getCurrentPoint() + earnedPoints);
                // Kiểm tra và nâng hạng nếu đủ điểm
                upgradeRankIfNeeded(user);
                userRepository.save(user);
            }
        }
    }

    /**
     * Khi admin CANCELLED:
     * - Nhả stockLock
     * - Hoàn usageLimit coupon nếu có
     */
    private void handleCancelled(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem item : items) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getProductVariantId())
                    .orElse(null);
            if (variant != null) {
                variant.setStockLock(Math.max(0, variant.getStockLock() - item.getQuantity()));
                productVariantRepository.save(variant);
            }
        }

        // Hoàn usageLimit coupon
        if (order.getCouponId() != null) {
            Coupon coupon = couponRepository.findById(order.getCouponId()).orElse(null);
            if (coupon != null) {
                coupon.setUsageLimit(coupon.getUsageLimit() + 1);
                couponRepository.save(coupon);
            }
        }
    }

    /**
     * Kiểm tra điểm hiện tại và nâng hạng nếu đủ điều kiện.
     * Rank threshold: BRONZE(0), SILVER(500), GOLD(2000), DIAMOND(5000)
     */
    private void upgradeRankIfNeeded(User user) {
        List<Rank> allRanks = rankRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Rank::getPoint).reversed())
                .collect(Collectors.toList());

        for (Rank rank : allRanks) {
            if (user.getCurrentPoint() >= rank.getPoint()) {
                if (user.getRank() == null || !user.getRank().getId().equals(rank.getId())) {
                    user.setRank(rank);
                }
                break;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatsResponse getOrderStats(LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (dateFrom == null) {
            dateFrom = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (dateTo == null) {
            dateTo = LocalDateTime.now();
        }

        BigDecimal totalRevenue = orderRepository.sumRevenueByDateRange(dateFrom, dateTo);
        List<Object[]> statusCounts = orderRepository.countByStatusAndDateRange(dateFrom, dateTo);

        Map<String, Long> orderCountByStatus = new LinkedHashMap<>();
        long totalOrders = 0;
        for (Object[] row : statusCounts) {
            String status = row[0].toString();
            Long count = (Long) row[1];
            orderCountByStatus.put(status, count);
            totalOrders += count;
        }

        return OrderStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .orderCountByStatus(orderCountByStatus)
                .build();
    }
}
