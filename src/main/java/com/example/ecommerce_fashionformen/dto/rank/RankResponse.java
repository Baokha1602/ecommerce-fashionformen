package com.example.ecommerce_fashionformen.dto.rank;

import com.example.ecommerce_fashionformen.domain.enums.RankName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankResponse {
    private Long id;
    private RankName rankName;
    private int point;
    private double rankDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
