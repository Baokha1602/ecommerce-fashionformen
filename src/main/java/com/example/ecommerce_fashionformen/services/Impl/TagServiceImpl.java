package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Tag;
import com.example.ecommerce_fashionformen.dto.request.TagRequest;
import com.example.ecommerce_fashionformen.dto.response.TagResponse;
import com.example.ecommerce_fashionformen.repository.TagRepository;
import com.example.ecommerce_fashionformen.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public TagResponse createTag(TagRequest request) {
        Tag entity = new Tag();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return mapToResponse(tagRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        Tag entity = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagResponse updateTag(Long id, TagRequest request) {
        Tag entity = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found with id: " + id));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return mapToResponse(tagRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag entity = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found with id: " + id));
        tagRepository.delete(entity);
    }

    private TagResponse mapToResponse(Tag entity) {
        TagResponse response = new TagResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        return response;
    }
}
