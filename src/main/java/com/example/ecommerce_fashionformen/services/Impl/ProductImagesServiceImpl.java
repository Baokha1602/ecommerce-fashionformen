package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.ProductImages;
import com.example.ecommerce_fashionformen.dto.request.ProductImagesRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductImagesResponse;
import com.example.ecommerce_fashionformen.repository.ProductImagesRepository;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.services.ProductImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImagesServiceImpl implements ProductImagesService {

    private final ProductImagesRepository productImagesRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductImagesResponse createProductImages(ProductImagesRequest request) {
        ProductImages entity = new ProductImages();
        entity.setImage(request.getImage());
        entity.setMainImage(request.isMainImage());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        return mapToResponse(productImagesRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImagesResponse getProductImagesById(Long id) {
        ProductImages entity = productImagesRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("ProductImages not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImagesResponse> getAllProductImagess() {
        return productImagesRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductImagesResponse updateProductImages(Long id, ProductImagesRequest request) {
        ProductImages entity = productImagesRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("ProductImages not found with id: " + id));
        entity.setImage(request.getImage());
        entity.setMainImage(request.isMainImage());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        return mapToResponse(productImagesRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProductImages(Long id) {
        ProductImages entity = productImagesRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("ProductImages not found with id: " + id));
        entity.softDelete();
        productImagesRepository.save(entity);
    }

    private ProductImagesResponse mapToResponse(ProductImages entity) {
        ProductImagesResponse response = new ProductImagesResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        response.setImage(entity.getImage());
        response.setMainImage(entity.isMainImage());
        return response;
    }
}
