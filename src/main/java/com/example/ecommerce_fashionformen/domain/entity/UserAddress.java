package com.example.ecommerce_fashionformen.domain.entity;

import com.example.ecommerce_fashionformen.domain.AuditableEntity;
import com.example.ecommerce_fashionformen.domain.enums.AddressType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_address")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserAddress extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 20)
    private AddressType addressType = AddressType.HOME;

    @Column(name = "province_id", nullable = false)
    private Long provinceId;

    @Column(name = "province_name", nullable = false, length = 255)
    private String provinceName;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "district_name", nullable = false, length = 255)
    private String districtName;

    @Column(name = "ward_id", nullable = false, length = 50)
    private String wardId;

    @Column(name = "ward_name", nullable = false, length = 255)
    private String wardName;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
