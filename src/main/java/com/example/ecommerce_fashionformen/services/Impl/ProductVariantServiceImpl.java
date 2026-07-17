package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariant;
import com.example.ecommerce_fashionformen.dto.request.ProductVariantRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantResponse;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.services.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantsRepository productVariantsRepository;
    private final ProductRepository productRepository;

    private ProductVariantResponse mapToResponse(ProductVariant entity) {
        ProductVariantResponse response = new ProductVariantResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) {
            response.setProductId(entity.getProduct().getId());
            response.setProductName(entity.getProduct().getName());
        }
        response.setName(entity.getName());
        response.setPrice(entity.getPrice());
        response.setDiscountPrice(entity.getDiscountPrice());
        response.setDiscountRate(entity.getDiscountRate());
        response.setStockTotal(entity.getStockTotal());
        response.setStockLock(entity.getStockLock());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> findAll() {
        return productVariantsRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse findById(Long id) {
        ProductVariant entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public ProductVariantResponse create(ProductVariantRequest request) {
        ProductVariant entity = new ProductVariant();
        entity.setProduct(productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + request.getProductId())));
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDiscountPrice(request.getDiscountPrice());
        entity.setDiscountRate(request.getDiscountRate());
        entity.setStockTotal(request.getStockTotal() != null ? request.getStockTotal() : 0);
        entity.setStockLock(request.getStockLock() != null ? request.getStockLock() : 0);
        return mapToResponse(productVariantsRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductVariantResponse update(Long id, ProductVariantRequest request) {
        ProductVariant entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + id));
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + request.getProductId())));
        }
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getPrice() != null) entity.setPrice(request.getPrice());
        if (request.getDiscountPrice() != null) entity.setDiscountPrice(request.getDiscountPrice());
        if (request.getDiscountRate() != null) entity.setDiscountRate(request.getDiscountRate());
        if (request.getStockTotal() != null) entity.setStockTotal(request.getStockTotal());
        if (request.getStockLock() != null) entity.setStockLock(request.getStockLock());
        return mapToResponse(productVariantsRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductVariant entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + id));
        productVariantsRepository.delete(entity);
    }
}
