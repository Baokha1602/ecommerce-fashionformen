package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.TagRequest;
import com.example.ecommerce_fashionformen.dto.response.TagResponse;
import java.util.List;

public interface TagService {
    TagResponse createTag(TagRequest request);
    TagResponse getTagById(Long id);
    List<TagResponse> getAllTags();
    TagResponse updateTag(Long id, TagRequest request);
    void deleteTag(Long id);
}
