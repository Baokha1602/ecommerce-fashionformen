package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.ProductReviews;
import com.example.ecommerce_fashionformen.dto.request.ProductReviewsRequest;
import com.example.ecommerce_fashionformen.dto.response.ProductReviewsResponse;
import com.example.ecommerce_fashionformen.repository.ProductReviewsRepository;
import com.example.ecommerce_fashionformen.repository.ProductRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.ProductReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewsServiceImpl implements ProductReviewsService {

    private final ProductReviewsRepository productReviewsRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductReviewsResponse createProductReviews(ProductReviewsRequest request) {
        ProductReviews entity = new ProductReviews();
        entity.setRating(request.getRating());
        entity.setTitle(request.getTitle());
        entity.setComment(request.getComment());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (request.getUserId() != null) {
            entity.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId())));
        } else {
            entity.setUser(null);
        }
        return mapToResponse(productReviewsRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewsResponse getProductReviewsById(Long id) {
        ProductReviews entity = productReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductReviews not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReviewsResponse> getAllProductReviewss() {
        return productReviewsRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductReviewsResponse updateProductReviews(Long id, ProductReviewsRequest request) {
        ProductReviews entity = productReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductReviews not found with id: " + id));
        entity.setRating(request.getRating());
        entity.setTitle(request.getTitle());
        entity.setComment(request.getComment());
        if (request.getProductId() != null) {
            entity.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (request.getUserId() != null) {
            entity.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId())));
        } else {
            entity.setUser(null);
        }
        return mapToResponse(productReviewsRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProductReviews(Long id) {
        ProductReviews entity = productReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProductReviews not found with id: " + id));
        productReviewsRepository.delete(entity);
    }

    private ProductReviewsResponse mapToResponse(ProductReviews entity) {
        ProductReviewsResponse response = new ProductReviewsResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        if (entity.getUser() != null) response.setUserId(entity.getUser().getId());
        response.setRating(entity.getRating());
        response.setTitle(entity.getTitle());
        response.setComment(entity.getComment());
        return response;
    }
}
