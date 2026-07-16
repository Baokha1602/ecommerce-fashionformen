package com.example.ecommerce_fashionformen.services.Impl;

import com.example.ecommerce_fashionformen.common.exception.NotFoundException;
import com.example.ecommerce_fashionformen.domain.entity.User;
import com.example.ecommerce_fashionformen.domain.entity.UserAddress;
import com.example.ecommerce_fashionformen.dto.address.UserAddressCreateRequest;
import com.example.ecommerce_fashionformen.dto.address.UserAddressResponse;
import com.example.ecommerce_fashionformen.dto.address.UserAddressUpdateRequest;
import com.example.ecommerce_fashionformen.repository.UserAddressRepository;
import com.example.ecommerce_fashionformen.repository.UserRepository;
import com.example.ecommerce_fashionformen.services.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    private UserAddressResponse mapToResponse(UserAddress address) {
        UserAddressResponse res = mapper.map(address, UserAddressResponse.class);
        if (address.getUser() != null) {
            res.setUserId(address.getUser().getId());
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return userAddressRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse findById(Long id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ với ID: " + id));
        return mapToResponse(address);
    }

    @Override
    @Transactional
    public UserAddressResponse create(UserAddressCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + request.getUserId()));

        UserAddress address = mapper.map(request, UserAddress.class);
        address.setUser(user);

        // Nếu đặt là địa chỉ mặc định → reset default cũ
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            resetDefaultAddress(user);
        }

        return mapToResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse update(Long id, UserAddressUpdateRequest request) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ với ID: " + id));

        if (request.getAddress() != null) {
            address.setAddress(request.getAddress());
        }
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }
        if (request.getProvinceId() != null) {
            address.setProvinceId(request.getProvinceId());
        }
        if (request.getProvinceName() != null) {
            address.setProvinceName(request.getProvinceName());
        }
        if (request.getDistrictId() != null) {
            address.setDistrictId(request.getDistrictId());
        }
        if (request.getDistrictName() != null) {
            address.setDistrictName(request.getDistrictName());
        }
        if (request.getWardId() != null) {
            address.setWardId(request.getWardId());
        }
        if (request.getWardName() != null) {
            address.setWardName(request.getWardName());
        }
        if (request.getIsDefault() != null) {
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                resetDefaultAddress(address.getUser());
            }
            address.setIsDefault(request.getIsDefault());
        }

        return mapToResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ với ID: " + id));
        userAddressRepository.delete(address);
    }

    /**
     * Reset địa chỉ mặc định cũ của user
     */
    private void resetDefaultAddress(User user) {
        userAddressRepository.findByUserAndIsDefaultTrue(user).ifPresent(existing -> {
            existing.setIsDefault(false);
            userAddressRepository.save(existing);
        });
    }
}
