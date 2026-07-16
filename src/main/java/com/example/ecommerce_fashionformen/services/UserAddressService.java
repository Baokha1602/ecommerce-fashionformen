package com.example.ecommerce_fashionformen.services;

import com.example.ecommerce_fashionformen.dto.address.UserAddressCreateRequest;
import com.example.ecommerce_fashionformen.dto.address.UserAddressResponse;
import com.example.ecommerce_fashionformen.dto.address.UserAddressUpdateRequest;

import java.util.List;

public interface UserAddressService {

    List<UserAddressResponse> findByUserId(Long userId);

    UserAddressResponse findById(Long id);

    UserAddressResponse create(UserAddressCreateRequest request);

    UserAddressResponse update(Long id, UserAddressUpdateRequest request);

    void delete(Long id);
}
