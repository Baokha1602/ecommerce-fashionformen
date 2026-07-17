package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Category;
import com.example.ecommerce_fashionformen.dto.request.CategoryRequest;
import com.example.ecommerce_fashionformen.dto.response.CategoryResponse;
import com.example.ecommerce_fashionformen.repository.CategoryRepository;
import com.example.ecommerce_fashionformen.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        category.setName(request.getName());

        if (request.getParentId() != null) {
            if (id.equals(request.getParentId())) {
                throw new BadRequestException("A category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
        }
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            response.setChildren(category.getChildren().stream()
                    .map(this::mapToResponseWithoutChildren)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private CategoryResponse mapToResponseWithoutChildren(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
        }
        return response;
    }
}
