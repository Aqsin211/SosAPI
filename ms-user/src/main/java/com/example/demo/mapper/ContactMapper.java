package com.example.demo.mapper;

import com.example.demo.dao.request.ContactRequest;
import com.example.demo.dao.response.ContactResponse;
import com.example.demo.entity.ContactEntity;
import com.example.demo.entity.UserEntity;

public enum ContactMapper {
    CONTACT_MAPPER;

    public static ContactEntity mapRequestToEntity(ContactRequest contactRequest, UserEntity userEntity) {
        return ContactEntity.builder()
                .gmail(contactRequest.getGmail())
                .phoneNumber(contactRequest.getPhoneNumber())
                .userEntity(userEntity)
                .name(contactRequest.getName())
                .role(contactRequest.getRole())
                .build();
    }

    public static ContactResponse mapEntityToResponse(ContactEntity contactEntity) {
        return ContactResponse.builder()
                .contactId(contactEntity.getContactId())
                .role(contactEntity.getRole())
                .gmail(contactEntity.getGmail())
                .phoneNumber(contactEntity.getPhoneNumber())
                .name(contactEntity.getName())
                .userId(contactEntity.getUserEntity().getUserId())
                .build();
    }
}
