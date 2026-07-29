package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Cart;
import com.example.ecommerce_fashionformen.domain.entity.CartItem;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.dto.cart.CartItemRequest;
import com.example.ecommerce_fashionformen.dto.cart.CartItemResponse;
import com.example.ecommerce_fashionformen.dto.cart.CartResponse;
import com.example.ecommerce_fashionformen.dto.promotion.DiscountResult;
import com.example.ecommerce_fashionformen.repository.CartItemRepository;
import com.example.ecommerce_fashionformen.repository.CartRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.repository.UserAddressRepository;
import com.example.ecommerce_fashionformen.services.CartService;
import com.example.ecommerce_fashionformen.services.DiscountCalculationService;
import com.example.ecommerce_fashionformen.services.GhnService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantsRepository productVariantRepository;
    private final DiscountCalculationService discountCalculationService;
    private final UserAddressRepository userAddressRepository;
    private final GhnService ghnService;
    private final ModelMapper mapper;

    @Value("${app.order.shipping-fee:30000}")
    private BigDecimal defaultShippingFee;

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartDetails(Long userId) {
        Cart cart = getOrCreateCart(userId);
        User user = cart.getUser();
        return buildCartResponse(cart, user);
    }

    @Override
    @Transactional
    public CartResponse addCartItem(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        // Kiểm tra tồn kho
        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new NotFoundException("Biến thể sản phẩm không tồn tại"));

        int availableStock = variant.getStockTotal() - variant.getStockLock();

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductVariantId(
                cart.getId(), request.getProductVariantId());

        int currentQtyInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int totalRequested = currentQtyInCart + request.getQuantity();

        if (totalRequested > availableStock) {
            throw new BadRequestException("Chỉ còn " + availableStock + " sản phẩm trong kho"
                    + (currentQtyInCart > 0 ? " (đã có " + currentQtyInCart + " trong giỏ)" : ""));
        }

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(totalRequested);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductVariantId(request.getProductVariantId());
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cart, cart.getUser());
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, CartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm trong giỏ không tồn tại"));

        // Nếu quantity = 0 → xóa item
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            Cart cart = cartItem.getCart();
            cartItemRepository.delete(cartItem);
            return buildCartResponse(cart, cart.getUser());
        }

        // Kiểm tra tồn kho
        ProductVariant variant = productVariantRepository.findById(cartItem.getProductVariantId())
                .orElseThrow(() -> new NotFoundException("Biến thể sản phẩm không tồn tại"));

        int availableStock = variant.getStockTotal() - variant.getStockLock();
        if (request.getQuantity() > availableStock) {
            throw new BadRequestException("Chỉ còn " + availableStock + " sản phẩm trong kho");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return buildCartResponse(cartItem.getCart(), cartItem.getCart().getUser());
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm trong giỏ không tồn tại"));
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart, cart.getUser());
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(Long userId, String couponCode) {
        Cart cart = getOrCreateCart(userId);
        // Lưu coupon vào Cart để bước tạo đơn tự lấy từ Cart
        cart.setAppliedCouponCode(couponCode);
        cartRepository.save(cart);
        return buildCartResponse(cart, cart.getUser());
    }

    @Override
    @Transactional
    public CartResponse removeCoupon(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
        return buildCartResponse(cart, cart.getUser());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart, User user) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(user.getId());
        response.setAppliedCouponCode(cart.getAppliedCouponCode());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;

        for (CartItem item : items) {
            CartItemResponse itemResp = new CartItemResponse();
            itemResp.setId(item.getId());
            itemResp.setCartId(cart.getId());
            itemResp.setProductVariantId(item.getProductVariantId());
            itemResp.setQuantity(item.getQuantity());
            itemResp.setCreatedAt(item.getCreatedAt());
            itemResp.setUpdatedAt(item.getUpdatedAt());

            Optional<ProductVariant> variantOpt = productVariantRepository.findById(item.getProductVariantId());

            if (variantOpt.isPresent()) {
                ProductVariant variant = variantOpt.get();
                itemResp.setProductVariantName(variant.getName());
                itemResp.setPrice(variant.getPrice());
                itemResp.setDiscountPrice(variant.getDiscountPrice());
                itemResp.setUnavailable(false);

                BigDecimal itemPrice = variant.getPrice();
                BigDecimal itemDiscountPrice = variant.getDiscountPrice() != null
                        ? variant.getDiscountPrice() : itemPrice;

                BigDecimal qty = new BigDecimal(item.getQuantity());
                subtotal = subtotal.add(itemPrice.multiply(qty));
                productDiscount = productDiscount.add(itemPrice.subtract(itemDiscountPrice).multiply(qty));
            } else {
                // Variant không tồn tại/ngừng bán → đánh dấu unavailable
                itemResp.setUnavailable(true);
            }

            itemResponses.add(itemResp);
        }
        
        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (totalQuantity > 0) {
            shippingFee = defaultShippingFee;
            List<com.example.ecommerce_fashionformen.domain.entity.UserAddress> addresses = userAddressRepository.findByUser(user);
            com.example.ecommerce_fashionformen.domain.entity.UserAddress addressToUse = null;
            for (com.example.ecommerce_fashionformen.domain.entity.UserAddress addr : addresses) {
                if (Boolean.TRUE.equals(addr.getIsDefault())) {
                    addressToUse = addr;
                    break;
                }
            }
            if (addressToUse == null && !addresses.isEmpty()) {
                addressToUse = addresses.get(0);
            }
            
            if (addressToUse != null) {
                try {
                    shippingFee = ghnService.calculateShippingFee(totalQuantity, addressToUse.getDistrictId().intValue(), addressToUse.getWardId());
                } catch (Exception e) {
                    // Ignore, fallback to default
                }
            }
        }

        response.setCartItems(itemResponses);
        response.setSubtotal(subtotal);
        response.setProductDiscount(productDiscount);
        response.setShippingFee(shippingFee);

        // Dùng chung DiscountCalculationService thay vì tính lặp lại logic coupon
        BigDecimal subtotalAfterProductDiscount = subtotal.subtract(productDiscount);
        DiscountResult discountResult = discountCalculationService.calculate(
                subtotalAfterProductDiscount,
                cart.getAppliedCouponCode(),
                user.getRank()
        );

        response.setCouponDiscount(discountResult.getCouponDiscount());
        response.setRankDiscount(discountResult.getRankDiscount());

        BigDecimal finalAmount = subtotalAfterProductDiscount
                .subtract(discountResult.getCouponDiscount())
                .subtract(discountResult.getRankDiscount())
                .add(shippingFee);

        // Đảm bảo finalAmount không âm
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        response.setFinalAmount(finalAmount);
        return response;
    }
}
