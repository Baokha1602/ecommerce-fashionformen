package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.Rank;
import com.example.ecommerce_fashionformen.dto.rank.RankResponse;
import com.example.ecommerce_fashionformen.dto.rank.RankUpsertRequest;
import com.example.ecommerce_fashionformen.repository.RankRepository;
import com.example.ecommerce_fashionformen.services.RankService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;
    private final ModelMapper mapper;

    private RankResponse mapToResponse(Rank rank) {
        return mapper.map(rank, RankResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankResponse> findAll() {
        return rankRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RankResponse findById(Long id) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hạng với ID: " + id));
        return mapToResponse(rank);
    }

    @Override
    @Transactional
    public RankResponse create(RankUpsertRequest request) {
        Rank rank = mapper.map(request, Rank.class);
        return mapToResponse(rankRepository.save(rank));
    }

    @Override
    @Transactional
    public RankResponse update(Long id, RankUpsertRequest request) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hạng với ID: " + id));

        rank.setRankName(request.getRankName());
        rank.setPoint(request.getPoint());
        rank.setRankDiscount(request.getRankDiscount());

        return mapToResponse(rankRepository.save(rank));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hạng với ID: " + id));
        rankRepository.delete(rank);
    }
}
