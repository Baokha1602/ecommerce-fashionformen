package com.example.ecommerce_fashionformen.dto.address;

import com.example.ecommerce_fashionformen.domain.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressCreateRequest {

    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255, message = "Địa chỉ chi tiết tối đa 255 ký tự")
    private String address;

    @NotNull(message = "Loại địa chỉ không được để trống")
    private AddressType addressType;

    @NotNull(message = "ID tỉnh/thành phố không được để trống")
    private Long provinceId;

    @NotBlank(message = "Tên tỉnh/thành phố không được để trống")
    @Size(max = 255, message = "Tên tỉnh/thành phố tối đa 255 ký tự")
    private String provinceName;

    @NotNull(message = "ID quận/huyện không được để trống")
    private Long districtId;

    @NotBlank(message = "Tên quận/huyện không được để trống")
    @Size(max = 255, message = "Tên quận/huyện tối đa 255 ký tự")
    private String districtName;

    @NotBlank(message = "ID phường/xã không được để trống")
    @Size(max = 50, message = "ID phường/xã tối đa 50 ký tự")
    private String wardId;

    @NotBlank(message = "Tên phường/xã không được để trống")
    @Size(max = 255, message = "Tên phường/xã tối đa 255 ký tự")
    private String wardName;

    private Boolean isDefault = false;
}
