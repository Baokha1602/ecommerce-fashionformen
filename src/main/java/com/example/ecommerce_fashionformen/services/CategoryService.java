package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.CategoryRequest;
import com.example.ecommerce_fashionformen.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
