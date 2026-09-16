package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.controllers.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Banner;
import com.example.ecommerce_fashionformen.dto.banner.BannerResponse;
import com.example.ecommerce_fashionformen.dto.banner.BannerUpsertRequest;
import com.example.ecommerce_fashionformen.repository.BannerRepository;
import com.example.ecommerce_fashionformen.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final ModelMapper mapper;

    private BannerResponse mapToResponse(Banner banner) {
        return mapper.map(banner, BannerResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> findAll() {
        return bannerRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> findAllActive() {
        return bannerRepository.findByIsActiveAndIsDeletedFalseOrderByDisplayOrderAsc(true).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponse findById(Long id) {
        Banner banner = bannerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy banner với ID: " + id));
        return mapToResponse(banner);
    }

    @Override
    @Transactional
    public BannerResponse create(BannerUpsertRequest request) {
        Banner banner = mapper.map(request, Banner.class);
        if (request.getIsActive() == null) {
            banner.setIsActive(true);
        }
        return mapToResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public BannerResponse update(Long id, BannerUpsertRequest request) {
        Banner banner = bannerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy banner với ID: " + id));

        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) {
            banner.setIsActive(request.getIsActive());
        }

        return mapToResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Banner banner = bannerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy banner với ID: " + id));
        banner.softDelete();
        bannerRepository.save(banner);
    }
}
