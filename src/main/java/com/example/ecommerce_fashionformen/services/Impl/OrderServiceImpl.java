package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.Order;
import com.example.ecommerce_fashionformen.domain.entity.OrderItem;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.order.OrderCreateRequest;
import com.example.ecommerce_fashionformen.dto.order.OrderResponse;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.repository.OrderItemRepository;
import com.example.ecommerce_fashionformen.repository.OrderRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantRepository;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.order.OrderItemRequest;
import com.example.ecommerce_fashionformen.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
                
        Order order = mapper.map(request, Order.class);
        order.setUser(user);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;
        
        // Cần lưu order để có ID cho OrderItem, nhưng tạm thời tính toán trước
        // Tuy nhiên do cần order_id, ta sẽ save tạm order, sau đó update lại các số tiền
        order = orderRepository.save(order);
        
        if (request.getOrderItems() != null) {
            for (OrderItemRequest itemReq : request.getOrderItems()) {
                OrderItem orderItem = mapper.map(itemReq, OrderItem.class);
                orderItem.setOrder(order);
                
                ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new NotFoundException("Biến thể sản phẩm không tồn tại"));
                
                BigDecimal itemPrice = variant.getPrice();
                BigDecimal itemDiscountPrice = variant.getDiscountPrice() != null ? variant.getDiscountPrice() : itemPrice;
                
                orderItem.setPrice(itemDiscountPrice); // Lưu giá thực tế mua
                
                BigDecimal qty = new BigDecimal(itemReq.getQuantity());
                subtotal = subtotal.add(itemPrice.multiply(qty));
                productDiscount = productDiscount.add(itemPrice.subtract(itemDiscountPrice).multiply(qty));
                
                orderItemRepository.save(orderItem);
            }
        }
        
        order.setSubtotalOriginal(subtotal);
        order.setProductDiscountAmount(productDiscount);
        
        BigDecimal rankDiscountAmount = BigDecimal.ZERO;
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(request.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (Boolean.TRUE.equals(coupon.getIsActive()) && 
                    LocalDateTime.now().isAfter(coupon.getStartDate()) && 
                    LocalDateTime.now().isBefore(coupon.getEndDate()) &&
                    subtotal.subtract(productDiscount).compareTo(coupon.getMinOrderValue()) >= 0) {
                    
                    order.setCouponId(coupon.getId());
                    BigDecimal discountableAmount = subtotal.subtract(productDiscount);
                    couponDiscountAmount = discountableAmount.multiply(coupon.getDiscountRate()).divide(new BigDecimal("100"));
                    
                    if (coupon.getMaxDiscountAmount() != null && couponDiscountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                        couponDiscountAmount = coupon.getMaxDiscountAmount();
                    }
                }
            }
        } else {
            Rank rank = user.getRank();
            if (rank != null && rank.getRankDiscount() != null) {
                BigDecimal discountableAmount = subtotal.subtract(productDiscount);
                rankDiscountAmount = discountableAmount.multiply(rank.getRankDiscount()).divide(new BigDecimal("100"));
            }
        }
        
        order.setCouponDiscountAmount(couponDiscountAmount);
        order.setRankDiscountAmount(rankDiscountAmount);
        
        BigDecimal shippingFee = new BigDecimal("30000");
        order.setShippingFeeOriginal(shippingFee);
        order.setShippingFeeActual(shippingFee);
        
        BigDecimal totalOrderAmount = subtotal.add(shippingFee);
        order.setTotalOrderAmount(totalOrderAmount);
        
        BigDecimal finalAmount = subtotal.subtract(productDiscount)
                .subtract(couponDiscountAmount)
                .subtract(rankDiscountAmount)
                .add(shippingFee);
        
        order.setFinalAmount(finalAmount);
        order = orderRepository.save(order);
        
        return mapper.map(order, OrderResponse.class);
    }

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
