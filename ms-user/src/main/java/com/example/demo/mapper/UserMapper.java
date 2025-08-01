package com.example.demo.mapper;

import com.example.demo.dao.request.UserRequest;
import com.example.demo.dao.response.ContactResponse;
import com.example.demo.dao.response.UserResponse;
import com.example.demo.entity.UserEntity;

import java.util.List;

public enum UserMapper {
    USER_MAPPER;

    public static UserEntity mapRequestToEntity(UserRequest userRequest) {
        return UserEntity.builder()
                .username(userRequest.getUsername())
                .gmail(userRequest.getGmail())
                .password(userRequest.getPassword())
                .phoneNumber(userRequest.getPhoneNumber())
                .role(userRequest.getRole())
                .build();
    }

    public static UserResponse mapEntityToResponse(UserEntity userEntity, List<ContactResponse> contacts) { //ListContacts
        return UserResponse.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .gmail(userEntity.getGmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .role(userEntity.getRole())
                .contacts(contacts)
                .build();
    }
}
