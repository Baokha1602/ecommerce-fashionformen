package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.request.RankRequest;
import com.example.ecommerce_fashionformen.dto.response.RankResponse;
import com.example.ecommerce_fashionformen.repository.RankRepository;
import com.example.ecommerce_fashionformen.services.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;

    @Override
    @Transactional
    public RankResponse createRank(RankRequest request) {
        Rank entity = new Rank();
        entity.setRankName(request.getRankName());
        entity.setPoint(request.getPoint());
        entity.setRankDiscount(request.getRankDiscount());
        return mapToResponse(rankRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RankResponse getRankById(Long id) {
        Rank entity = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rank not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankResponse> getAllRanks() {
        return rankRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RankResponse updateRank(Long id, RankRequest request) {
        Rank entity = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rank not found with id: " + id));
        entity.setRankName(request.getRankName());
        entity.setPoint(request.getPoint());
        entity.setRankDiscount(request.getRankDiscount());
        return mapToResponse(rankRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteRank(Long id) {
        Rank entity = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rank not found with id: " + id));
        rankRepository.delete(entity);
    }

    private RankResponse mapToResponse(Rank entity) {
        RankResponse response = new RankResponse();
        response.setId(entity.getId());
        response.setRankName(entity.getRankName());
        response.setPoint(entity.getPoint());
        response.setRankDiscount(entity.getRankDiscount());
        return response;
    }
}
