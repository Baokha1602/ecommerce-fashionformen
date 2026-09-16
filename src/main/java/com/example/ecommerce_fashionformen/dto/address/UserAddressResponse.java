package com.example.ecommerce_fashionformen.dto.address;

import com.example.ecommerce_fashionformen.domain.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponse {

    private Long id;
    private Long userId;
    private String address;
    private AddressType addressType;
    private Long provinceId;
    private String provinceName;
    private Long districtId;
    private String districtName;
    private String wardId;
    private String wardName;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
