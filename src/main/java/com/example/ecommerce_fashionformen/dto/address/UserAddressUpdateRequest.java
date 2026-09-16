package com.example.ecommerce_fashionformen.dto.address;

import com.example.ecommerce_fashionformen.domain.enums.AddressType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressUpdateRequest {

    @Size(max = 255, message = "Địa chỉ chi tiết tối đa 255 ký tự")
    private String address;

    private AddressType addressType;

    private Long provinceId;

    @Size(max = 255, message = "Tên tỉnh/thành phố tối đa 255 ký tự")
    private String provinceName;

    private Long districtId;

    @Size(max = 255, message = "Tên quận/huyện tối đa 255 ký tự")
    private String districtName;

    @Size(max = 50, message = "ID phường/xã tối đa 50 ký tự")
    private String wardId;

    @Size(max = 255, message = "Tên phường/xã tối đa 255 ký tự")
    private String wardName;

    private Boolean isDefault;
}
