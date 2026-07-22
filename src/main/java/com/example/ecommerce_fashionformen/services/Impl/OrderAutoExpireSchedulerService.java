package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.*;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.domain.enums.PaymentMethod;
import com.example.ecommerce_fashionformen.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Job chạy định kỳ mỗi 5 phút để tự hủy đơn thanh toán online quá thời gian chờ.
 * Điều kiện: PENDING + (MOMO/VN_PAY) + isPaid=false + createdAt < now - timeout
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAutoExpireSchedulerService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Value("${app.order.payment-timeout-minutes:15}")
    private int paymentTimeoutMinutes;

    @Scheduled(fixedRate = 300000) // 5 phút
    @Transactional
    public void autoExpireUnpaidOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(paymentTimeoutMinutes);

        List<Order> expiredOrders = orderRepository
                .findByOrderStatusAndPaymentMethodInAndIsPaidFalseAndCreatedAtBefore(
                        OrderStatus.PENDING,
                        List.of(PaymentMethod.MOMO, PaymentMethod.VN_PAY),
                        cutoff
                );

        for (Order order : expiredOrders) {
            try {
                // Nhả stockLock
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

                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                // Lưu status history
                OrderStatusHistory history = OrderStatusHistory.builder()
                        .order(order)
                        .status(OrderStatus.CANCELLED)
                        .changedBy("SYSTEM")
                        .changedAt(LocalDateTime.now())
                        .note("Tự động hủy do quá thời gian thanh toán (" + paymentTimeoutMinutes + " phút)")
                        .build();
                orderStatusHistoryRepository.save(history);

                log.info("Tự động hủy đơn #{} do quá hạn thanh toán online", order.getId());
            } catch (Exception e) {
                log.error("Lỗi khi tự động hủy đơn #{}: {}", order.getId(), e.getMessage());
            }
        }
    }
}
