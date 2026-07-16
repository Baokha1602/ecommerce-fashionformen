package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.tag.TagCreateRequest;
import com.example.ecommerce_fashionformen.dto.tag.TagResponse;
import com.example.ecommerce_fashionformen.dto.tag.TagUpdateRequest;

import java.util.List;

public interface TagService {

    List<TagResponse> findAll();

    TagResponse findById(Long id);

    TagResponse create(TagCreateRequest request);

    TagResponse update(Long id, TagUpdateRequest request);

    void delete(Long id);
}
