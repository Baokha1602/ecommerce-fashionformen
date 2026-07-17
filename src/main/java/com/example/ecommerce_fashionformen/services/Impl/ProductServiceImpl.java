package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Product;
import com.example.ecommerce_fashionformen.dto.request.ProductRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductResponse;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.repository.CategoryRepository;
import com.example.ecommerce_fashionformen.repository.BrandRepository;
import com.example.ecommerce_fashionformen.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product entity = new Product();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        if (request.getCategoryId() != null) {
            entity.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId())));
        } else {
            entity.setCategory(null);
        }
        if (request.getBrandId() != null) {
            entity.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found with id: " + request.getBrandId())));
        } else {
            entity.setBrand(null);
        }
        return mapToResponse(productRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        if (request.getCategoryId() != null) {
            entity.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId())));
        } else {
            entity.setCategory(null);
        }
        if (request.getBrandId() != null) {
            entity.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found with id: " + request.getBrandId())));
        } else {
            entity.setBrand(null);
        }
        return mapToResponse(productRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        productRepository.delete(entity);
    }

    private ProductResponse mapToResponse(Product entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        if (entity.getCategory() != null) response.setCategoryId(entity.getCategory().getId());
        if (entity.getBrand() != null) response.setBrandId(entity.getBrand().getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setSoldQuantity(entity.getSoldQuantity());
        return response;
    }
}
