package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.ProductTag;
import com.example.ecommerce_fashionformen.dto.request.ProductTagRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductTagResponse;
import com.example.ecommerce_fashionformen.repository.ProductTagRepository;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.repository.TagRepository;
import com.example.ecommerce_fashionformen.services.ProductTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTagServiceImpl implements ProductTagService {

    private final ProductTagRepository productTagRepository;
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public ProductTagResponse createProductTag(ProductTagRequest request) {
        ProductTag entity = new ProductTag();
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (request.getTagId() != null) {
            entity.setTag(tagRepository.findById(request.getTagId())
                    .orElseThrow(() -> new NotFoundException("Tag not found with id: " + request.getTagId())));
        } else {
            entity.setTag(null);
        }
        return mapToResponse(productTagRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductTagResponse getProductTagById(Long id) {
        ProductTag entity = productTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductTag not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTagResponse> getAllProductTags() {
        return productTagRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductTagResponse updateProductTag(Long id, ProductTagRequest request) {
        ProductTag entity = productTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductTag not found with id: " + id));
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (request.getTagId() != null) {
            entity.setTag(tagRepository.findById(request.getTagId())
                    .orElseThrow(() -> new NotFoundException("Tag not found with id: " + request.getTagId())));
        } else {
            entity.setTag(null);
        }
        return mapToResponse(productTagRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProductTag(Long id) {
        ProductTag entity = productTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductTag not found with id: " + id));
        productTagRepository.delete(entity);
    }

    private ProductTagResponse mapToResponse(ProductTag entity) {
        ProductTagResponse response = new ProductTagResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        if (entity.getTag() != null) response.setTagId(entity.getTag().getId());
        return response;
    }
}
