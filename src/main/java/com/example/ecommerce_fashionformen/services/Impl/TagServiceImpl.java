package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.ConflictException;
import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Tag;
import com.example.ecommerce_fashionformen.dto.tag.TagCreateRequest;
import com.example.ecommerce_fashionformen.dto.tag.TagResponse;
import com.example.ecommerce_fashionformen.dto.tag.TagUpdateRequest;
import com.example.ecommerce_fashionformen.repository.TagRepository;
import com.example.ecommerce_fashionformen.services.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper mapper;

    private TagResponse mapToResponse(Tag tag) {
        return mapper.map(tag, TagResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> findAll() {
        return tagRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thẻ tag với ID: " + id));
        return mapToResponse(tag);
    }

    @Override
    @Transactional
    public TagResponse create(TagCreateRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên thẻ tag đã tồn tại: " + request.getName());
        }

        Tag tag = mapper.map(request, Tag.class);
        return mapToResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public TagResponse update(Long id, TagUpdateRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thẻ tag với ID: " + id));

        // Kiểm tra trùng tên với tag khác
        if (request.getName() != null && !request.getName().equals(tag.getName()) && tagRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên thẻ tag đã tồn tại: " + request.getName());
        }

        if (request.getName() != null) {
            tag.setName(request.getName());
        }
        if (request.getDescription() != null) {
            tag.setDescription(request.getDescription());
        }

        return mapToResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thẻ tag với ID: " + id));
        tagRepository.delete(tag);
    }
}
