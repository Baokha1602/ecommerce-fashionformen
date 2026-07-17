package com.example.ecommerce_fashionformen.controller;

import com.example.ecommerce_fashionformen.common.ApiResponse;
import com.example.ecommerce_fashionformen.dto.request.RankRequest;
import com.example.ecommerce_fashionformen.dto.response.RankResponse;
import com.example.ecommerce_fashionformen.services.RankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranks")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @PostMapping
    public ResponseEntity<ApiResponse<RankResponse>> createRank(@Valid @RequestBody RankRequest request) {
        RankResponse response = rankService.createRank(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Rank created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RankResponse>> getRankById(@PathVariable Long id) {
        RankResponse response = rankService.getRankById(id);
        return ResponseEntity.ok(ApiResponse.success("Rank retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RankResponse>>> getAllRanks() {
        return ResponseEntity.ok(ApiResponse.success("Ranks retrieved successfully", rankService.getAllRanks()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RankResponse>> updateRank(
            @PathVariable Long id,
            @Valid @RequestBody RankRequest request) {
        RankResponse response = rankService.updateRank(id, request);
        return ResponseEntity.ok(ApiResponse.success("Rank updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRank(@PathVariable Long id) {
        rankService.deleteRank(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Rank deleted successfully"));
    }
}
