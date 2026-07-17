package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.ProductVariants;
import com.example.ecommerce_fashionformen.dto.request.ProductVariantsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductVariantsResponse;
import com.example.ecommerce_fashionformen.repository.ProductVariantsRepository;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.services.ProductVariantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantsServiceImpl implements ProductVariantsService {

    private final ProductVariantsRepository productVariantsRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductVariantsResponse createProductVariants(ProductVariantsRequest request) {
        ProductVariants entity = new ProductVariants();
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDiscountPrice(request.getDiscountPrice());
        entity.setDiscountRate(request.getDiscountRate());
        entity.setStockTotal(request.getStockTotal());
        entity.setStockLock(request.getStockLock());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        return mapToResponse(productVariantsRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantsResponse getProductVariantsById(Long id) {
        ProductVariants entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductVariants not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantsResponse> getAllProductVariantss() {
        return productVariantsRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductVariantsResponse updateProductVariants(Long id, ProductVariantsRequest request) {
        ProductVariants entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductVariants not found with id: " + id));
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDiscountPrice(request.getDiscountPrice());
        entity.setDiscountRate(request.getDiscountRate());
        entity.setStockTotal(request.getStockTotal());
        entity.setStockLock(request.getStockLock());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        return mapToResponse(productVariantsRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProductVariants(Long id) {
        ProductVariants entity = productVariantsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductVariants not found with id: " + id));
        productVariantsRepository.delete(entity);
    }

    private ProductVariantsResponse mapToResponse(ProductVariants entity) {
        ProductVariantsResponse response = new ProductVariantsResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        response.setName(entity.getName());
        response.setPrice(entity.getPrice());
        response.setDiscountPrice(entity.getDiscountPrice());
        response.setDiscountRate(entity.getDiscountRate());
        response.setStockTotal(entity.getStockTotal());
        response.setStockLock(entity.getStockLock());
        return response;
    }
}
