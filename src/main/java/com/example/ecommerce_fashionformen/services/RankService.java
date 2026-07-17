package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.request.RankRequest;
import com.example.ecommerce_fashionformen.dto.response.RankResponse;
import java.util.List;

public interface RankService {
    RankResponse createRank(RankRequest request);
    RankResponse getRankById(Long id);
    List<RankResponse> getAllRanks();
    RankResponse updateRank(Long id, RankRequest request);
    void deleteRank(Long id);
}
