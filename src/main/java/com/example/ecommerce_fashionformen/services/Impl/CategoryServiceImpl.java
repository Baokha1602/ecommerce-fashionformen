package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.BadRequestException;
import com.example.ecommerce_fashionformen.common.exception.ConflictException;
import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Category;
import com.example.ecommerce_fashionformen.dto.category.CategoryResponse;
import com.example.ecommerce_fashionformen.dto.category.CategoryUpsertRequest;
import com.example.ecommerce_fashionformen.repository.CategoryRepository;
import com.example.ecommerce_fashionformen.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse res = mapper.map(category, CategoryResponse.class);
        if (category.getParent() != null) {
            res.setParentId(category.getParent().getId());
            res.setParentName(category.getParent().getName());
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục với ID: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryUpsertRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại: " + request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId()));
            category.setParent(parent);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryUpsertRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra trùng tên với category khác
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại: " + request.getName());
        }

        // Không cho phép set parent là chính mình
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new BadRequestException("Danh mục không thể là cha của chính nó");
        }

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục với ID: " + id));
        categoryRepository.delete(category);
    }
}
