package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.category.CategoryResponse;
import com.example.ecommerce_fashionformen.dto.category.CategoryUpsertRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse create(CategoryUpsertRequest request);

    CategoryResponse update(Long id, CategoryUpsertRequest request);

    void delete(Long id);
}
