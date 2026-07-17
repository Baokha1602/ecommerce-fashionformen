package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.domain.entity.Cart;
import com.example.ecommerce_fashionformen.domain.entity.CartItem;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.cart.CartItemRequest;
import com.example.ecommerce_fashionformen.dto.cart.CartResponse;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.repository.CartItemRepository;
import com.example.ecommerce_fashionformen.repository.CartRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantRepository;
import com.example.ecommerce_fashionformen.repository.CouponRepository;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.entity.Coupon;
import com.example.ecommerce_fashionformen.dto.cart.CartItemResponse;
import com.example.ecommerce_fashionformen.services.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper mapper;

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        
        return cartRepository.findAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartResponse getCartDetails(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse addCartItem(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = mapper.map(request, CartItem.class);
            cartItem.setCart(cart);
            cartItemRepository.save(cartItem);
        }
        
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, CartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm trong giỏ không tồn tại"));
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return buildCartResponse(cartItem.getCart(), null);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm trong giỏ không tồn tại"));
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart, null);
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(Long userId, String couponCode) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart, couponCode);
    }

    private CartResponse buildCartResponse(Cart cart, String couponCode) {
        CartResponse response = mapper.map(cart, CartResponse.class);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;
        
        for (CartItem item : items) {
            CartItemResponse itemResp = mapper.map(item, CartItemResponse.class);
            ProductVariant variant = productVariantRepository.findById(item.getProductVariantId()).orElse(null);
            
            if (variant != null) {
                itemResp.setPrice(variant.getPrice());
                itemResp.setDiscountPrice(variant.getDiscountPrice());
                
                BigDecimal itemPrice = variant.getPrice();
                BigDecimal itemDiscountPrice = variant.getDiscountPrice() != null ? variant.getDiscountPrice() : itemPrice;
                
                BigDecimal qty = new BigDecimal(item.getQuantity());
                subtotal = subtotal.add(itemPrice.multiply(qty));
                productDiscount = productDiscount.add(itemPrice.subtract(itemDiscountPrice).multiply(qty));
            }
            itemResponses.add(itemResp);
        }
        
        response.setCartItems(itemResponses);
        response.setSubtotal(subtotal);
        response.setProductDiscount(productDiscount);
        
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(couponCode);
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (Boolean.TRUE.equals(coupon.getIsActive()) && 
                    LocalDateTime.now().isAfter(coupon.getStartDate()) && 
                    LocalDateTime.now().isBefore(coupon.getEndDate()) &&
                    subtotal.subtract(productDiscount).compareTo(coupon.getMinOrderValue()) >= 0) {
                    
                    BigDecimal discountableAmount = subtotal.subtract(productDiscount);
                    couponDiscount = discountableAmount.multiply(coupon.getDiscountRate()).divide(new BigDecimal("100"));
                    
                    if (coupon.getMaxDiscountAmount() != null && couponDiscount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                        couponDiscount = coupon.getMaxDiscountAmount();
                    }
                }
            }
        }
        
        response.setCouponDiscount(couponDiscount);
        response.setFinalAmount(subtotal.subtract(productDiscount).subtract(couponDiscount));
        
        return response;
    }
}
