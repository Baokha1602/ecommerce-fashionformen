package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.services.GhnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class GhnAddressController {

    private final GhnService ghnService;

    @GetMapping("/provinces")
    public ApiResponse<Object> getProvinces() {
        return ApiResponse.success("Lấy danh sách tỉnh thành thành công", ghnService.getProvinces());
    }

    @GetMapping("/districts")
    public ApiResponse<Object> getDistricts(@RequestParam int provinceId) {
        return ApiResponse.success("Lấy danh sách quận huyện thành công", ghnService.getDistricts(provinceId));
    }

    @GetMapping("/wards")
    public ApiResponse<Object> getWards(@RequestParam int districtId) {
        return ApiResponse.success("Lấy danh sách phường xã thành công", ghnService.getWards(districtId));
    }
}
