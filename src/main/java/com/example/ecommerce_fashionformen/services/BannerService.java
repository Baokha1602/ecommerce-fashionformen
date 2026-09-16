package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.banner.BannerResponse;
import com.example.ecommerce_fashionformen.dto.banner.BannerUpsertRequest;

import java.util.List;

public interface BannerService {

    List<BannerResponse> findAll();

    List<BannerResponse> findAllActive();

    BannerResponse findById(Long id);

    BannerResponse create(BannerUpsertRequest request);

    BannerResponse update(Long id, BannerUpsertRequest request);

    void delete(Long id);
}
