package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.*;
import com.example.ecommerce_fashionformen.domain.enums.OrderStatus;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.dto.promotion.DiscountResult;
import com.example.ecommerce_fashionformen.repository.*;
import com.example.ecommerce_fashionformen.services.CartService;
import com.example.ecommerce_fashionformen.services.DiscountCalculationService;
import com.example.ecommerce_fashionformen.services.GhnService;
import com.example.ecommerce_fashionformen.services.NotificationService;
import com.example.ecommerce_fashionformen.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CouponUsageHistoryRepository couponUsageHistoryRepository;
    private final UserAddressRepository userAddressRepository;
    private final DiscountCalculationService discountCalculationService;
    private final GhnService ghnService;
    private final NotificationService notificationService;
    private final ModelMapper mapper;

    @Value("${app.order.shipping-fee:30000}")
    private BigDecimal defaultShippingFee;

    /**
     * Tạo đơn hàng theo transaction chặt chẽ:
     * 1. Lấy Cart → CartItems (không nhận orderItems từ request)
     * 2. Lock + kiểm tra tồn kho từng variant (PESSIMISTIC_WRITE)
     * 3. Tính subtotal, productDiscount
     * 4. DiscountCalculationService cho coupon/rank
     * 5. Tạo Order + OrderItems
     * 6. Tăng stockLock, giảm usageLimit coupon
     * 7. Xóa CartItems
     * 8. Rollback toàn bộ nếu lỗi
     */
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        // 1. Lấy giỏ hàng từ Cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Giỏ hàng không tồn tại"));

        // Lấy tất cả CartItem của user và LỌC theo selectedCartItemIds
        List<CartItem> allCartItems = cartItemRepository.findByCartId(cart.getId());

        List<CartItem> selectedCartItems = allCartItems.stream()
                .filter(item -> request.getSelectedCartItemIds().contains(item.getId()))
                .toList();

        // Kiểm tra tính hợp lệ của danh sách chọn
        if (selectedCartItems.isEmpty()) {
            throw new BadRequestException("Không tìm thấy sản phẩm nào được chọn trong giỏ hàng");
        }
        if (selectedCartItems.size() != request.getSelectedCartItemIds().size()) {
            throw new BadRequestException("Một số sản phẩm được chọn không hợp lệ hoặc không thuộc giỏ hàng của bạn");
        }

        // 2. Lock và kiểm tra tồn kho từng variant (Chỉ lặp qua selectedCartItems)
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;
        List<ProductVariant> lockedVariants = new ArrayList<>();

        for (CartItem cartItem : selectedCartItems) {
            // Pessimistic lock — SELECT ... FOR UPDATE
            ProductVariant variant = productVariantRepository.findByIdForUpdate(cartItem.getProductVariantId())
                    .orElseThrow(() -> new NotFoundException(
                            "Biến thể sản phẩm ID " + cartItem.getProductVariantId() + " không tồn tại"));

            int availableStock = variant.getStockTotal() - variant.getStockLock();
            if (cartItem.getQuantity() > availableStock) {
                throw new BadRequestException(
                        "Sản phẩm \"" + variant.getName() + "\" chỉ còn " + availableStock
                                + " trong kho, không đủ cho số lượng yêu cầu " + cartItem.getQuantity());
            }

            // 3. Tính subtotal và productDiscount
            BigDecimal itemPrice = variant.getPrice();
            BigDecimal itemDiscountPrice = variant.getDiscountPrice() != null
                    ? variant.getDiscountPrice() : itemPrice;
            BigDecimal qty = new BigDecimal(cartItem.getQuantity());

            subtotal = subtotal.add(itemPrice.multiply(qty));
            productDiscount = productDiscount.add(itemPrice.subtract(itemDiscountPrice).multiply(qty));

            lockedVariants.add(variant);
        }

        // 4. Tính coupon/rank discount dùng DiscountCalculationService
        BigDecimal subtotalAfterProductDiscount = subtotal.subtract(productDiscount);
        String couponCode = cart.getAppliedCouponCode();

        DiscountResult discountResult = discountCalculationService.calculate(
                subtotalAfterProductDiscount, couponCode, user.getRank());

        if (couponCode != null && !couponCode.isEmpty() && !discountResult.isSuccess()) {
            throw new BadRequestException("Coupon không hợp lệ: " + discountResult.getErrorMessage());
        }

        // 5. Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setUserAddressId(request.getUserAddressId());
        order.setFirstName(request.getFirstName());
        order.setLastName(request.getLastName());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setEmail(request.getEmail());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setIsPaid(false);

        order.setSubtotalOriginal(subtotal);
        order.setProductDiscountAmount(productDiscount);
        order.setCouponDiscountAmount(discountResult.getCouponDiscount());
        order.setRankDiscountAmount(discountResult.getRankDiscount());
        order.setCouponId(discountResult.getAppliedCouponId());
        order.setCouponCode(couponCode);

        // Tính phí ship tự động (dựa trên số lượng của selectedCartItems)
        UserAddress userAddress = userAddressRepository.findById(request.getUserAddressId())
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        int totalQuantity = selectedCartItems.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal actualShippingFee = BigDecimal.ZERO;
        if (totalQuantity > 0) {
            try {
                actualShippingFee = ghnService.calculateShippingFee(totalQuantity, userAddress.getDistrictId().intValue(), userAddress.getWardId());
            } catch (Exception e) {
                actualShippingFee = defaultShippingFee;
            }
        }

        order.setShippingFeeOriginal(actualShippingFee);
        order.setShippingFeeActual(actualShippingFee);

        BigDecimal totalOrderAmount = subtotal.add(actualShippingFee);
        order.setTotalOrderAmount(totalOrderAmount);

        BigDecimal finalAmount = subtotalAfterProductDiscount
                .subtract(discountResult.getCouponDiscount())
                .subtract(discountResult.getRankDiscount())
                .add(actualShippingFee);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        order.setFinalAmount(finalAmount);

        order = orderRepository.save(order);

        // Tạo OrderItems
        for (int i = 0; i < selectedCartItems.size(); i++) {
            CartItem cartItem = selectedCartItems.get(i);
            ProductVariant variant = lockedVariants.get(i);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariantId(cartItem.getProductVariantId());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal effectivePrice = variant.getDiscountPrice() != null
                    ? variant.getDiscountPrice() : variant.getPrice();
            orderItem.setPrice(effectivePrice);

            orderItemRepository.save(orderItem);
        }

        // 6. Tăng stockLock — giữ chỗ hàng
        for (int i = 0; i < selectedCartItems.size(); i++) {
            ProductVariant variant = lockedVariants.get(i);
            CartItem cartItem = selectedCartItems.get(i);
            variant.setStockLock(variant.getStockLock() + cartItem.getQuantity());
            productVariantRepository.save(variant);
        }

        // 7. Giảm usageLimit coupon (pessimistic lock)
        if (discountResult.getAppliedCouponId() != null) {
            Coupon coupon = couponRepository.findByCodeForUpdate(couponCode)
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại"));

            if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
                throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng");
            }

            coupon.setUsageLimit(coupon.getUsageLimit() - 1);
            couponRepository.save(coupon);

            CouponUsageHistory usage = CouponUsageHistory.builder()
                    .coupon(coupon)
                    .order(order)
                    .user(user)
                    .usedAt(LocalDateTime.now())
                    .build();
            couponUsageHistoryRepository.save(usage);
        }

        // Lưu OrderStatusHistory
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .changedBy(user.getUsername())
                .changedAt(LocalDateTime.now())
                .note("Đơn hàng được tạo")
                .build();
        orderStatusHistoryRepository.save(statusHistory);

        // YÊU CHỈ XÓA CÁC CartItem ĐÃ ĐƯỢC CHỌN KHỎI DB
        cartItemRepository.deleteAll(selectedCartItems);

        // Reset coupon khỏi giỏ hàng vì mã này đã được gắn vào Order
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);

        // 9. Thông báo cho ADMIN/STAFF
        final Long savedOrderId = order.getId();
        final String customerName = user.getFullName();
        try {
            notificationService.notifyOrderPlaced(savedOrderId, customerName);
        } catch (Exception e) {
            log.warn("[NOTIFICATION] Không thể gửi thông báo đơn mới #{}: {}", savedOrderId, e.getMessage());
        }

        return mapper.map(order, OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(o -> mapper.map(o, OrderResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        // Kiểm tra ownership (chỉ xem đơn của mình)
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xem đơn hàng này");
        }

        return mapper.map(order, OrderResponse.class);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền hủy đơn hàng này");
        }

        // Chỉ cho hủy khi đơn ở PENDING hoặc PROCESSING
        if (order.getOrderStatus() != OrderStatus.PENDING
                && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new BadRequestException("Không thể hủy đơn hàng ở trạng thái "
                    + order.getOrderStatus().name() + ". Chỉ có thể hủy khi đơn ở trạng thái PENDING hoặc PROCESSING");
        }

        // Nhả stockLock
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getProductVariantId())
                    .orElse(null);
            if (variant != null) {
                variant.setStockLock(Math.max(0, variant.getStockLock() - item.getQuantity()));
                productVariantRepository.save(variant);
            }
        }

        // Hoàn usageLimit coupon nếu có
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
        User user = userRepository.findById(userId).orElse(null);
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .changedBy(user != null ? user.getUsername() : "customer")
                .changedAt(LocalDateTime.now())
                .note("Khách hàng tự hủy đơn")
                .build();
        orderStatusHistoryRepository.save(statusHistory);

        // Thông báo cho ADMIN/STAFF về đơn bị hủy bởi khách (fire-and-forget)
        final String cancelledByName = user != null
                ? user.getFullName() + " (Khách hàng)" : "Khách hàng";
        try {
            notificationService.notifyOrderCancelled(orderId, cancelledByName);
        } catch (Exception e) {
            log.warn("[NOTIFICATION] Không thể gửi thông báo hủy đơn #{}: {}", orderId, e.getMessage());
        }

        return mapper.map(order, OrderResponse.class);
    }

    @Override
    public BigDecimal calculateShippingFee(Long addressId, Integer totalQuantity) {
        if (totalQuantity == null || totalQuantity <= 0) {
            return BigDecimal.ZERO;
        }

        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        try {
            return ghnService.calculateShippingFee(
                    totalQuantity,
                    userAddress.getDistrictId().intValue(),
                    userAddress.getWardId()
            );
        } catch (Exception e) {
            log.error("Lỗi gọi GHN tính phí ship, dùng phí mặc định: {}", e.getMessage());
            return defaultShippingFee;
        }
    }

}
