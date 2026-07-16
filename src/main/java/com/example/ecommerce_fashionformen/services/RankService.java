package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.rank.RankResponse;
import com.example.ecommerce_fashionformen.dto.rank.RankUpsertRequest;

import java.util.List;

public interface RankService {

    List<RankResponse> findAll();

    RankResponse findById(Long id);

    RankResponse create(RankUpsertRequest request);

    RankResponse update(Long id, RankUpsertRequest request);

    RankResponse patch(Long id, RankUpsertRequest request);

    void delete(Long id);
}
