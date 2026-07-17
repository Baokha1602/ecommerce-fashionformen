package com.example.ecommerce_fashionformen.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankResponse {
    private Long id;
    private String rankName;
    private int point;
    private int rankDiscount;
}
