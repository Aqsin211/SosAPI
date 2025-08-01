package az.company.mssos.mapper;

import az.company.mssos.dao.response.ContactResponse;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.entity.UserEntity;

import java.util.List;

public enum UserMapper {
    USER_MAPPER;

    public static UserEntity mapResponseToEntity(UserResponse userResponse) { //ListContacts
        return UserEntity.builder()
                .userId(userResponse.getUserId())
                .username(userResponse.getUsername())
                .gmail(userResponse.getGmail())
                .phoneNumber(userResponse.getPhoneNumber())
                .role(userResponse.getRole())
                .build();
    }
}