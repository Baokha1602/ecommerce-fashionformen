package com.example.ecommerce_fashionformen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankRequest {
    @NotBlank(message = "Rank name is required")
    private String rankName;
    private int point;
    private int rankDiscount;
}
