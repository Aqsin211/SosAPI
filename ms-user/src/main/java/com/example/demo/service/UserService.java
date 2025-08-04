package com.example.demo.service;

import com.example.demo.dao.request.AuthRequest;
import com.example.demo.dao.request.UserRequest;
import com.example.demo.dao.response.UserResponse;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.ErrorMessages;
import com.example.demo.enums.UserRoles;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.UserExistsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ContactService contactService;

    public UserService(UserRepository userRepository, ContactService contactService) {
        this.userRepository = userRepository;
        this.contactService = contactService;
    }

    public void createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage());
        }
        if (userRepository.existsByGmail(userRequest.getGmail())) {
            throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage());
        }
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage());
        }
        userRequest.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
        userRequest.setRole(UserRoles.USER.getRole());
        userRepository.save(UserMapper.mapRequestToEntity(userRequest));
    }

    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId).map((UserEntity userEntity) -> UserMapper.mapEntityToResponse(userEntity, contactService.getAllContactsByUserId(userId))).orElseThrow(
                () -> new NotFoundException(ErrorMessages.USER_DOES_NOT_EXIST.getMessage())
        );
    }

    public void updateUser(Long userId, UserRequest userRequest) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessages.USER_DOES_NOT_EXIST.getMessage()));
        if (userRepository.existsByUsername(userRequest.getUsername()) && !Objects.equals(userEntity.getUsername(), userRequest.getUsername())) {
            throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage());
        } else {
            userEntity.setUsername(userRequest.getUsername());
        }
        if (userRepository.existsByGmail(userRequest.getGmail()) && !Objects.equals(userEntity.getGmail(), userRequest.getGmail())) {
            throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage());
        } else {
            userEntity.setGmail(userRequest.getGmail());
        }
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber()) && !Objects.equals(userEntity.getPhoneNumber(), userRequest.getPhoneNumber())) {
            throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage());
        } else {
            userEntity.setPhoneNumber(userRequest.getPhoneNumber());
        }
        userEntity.setRole(UserRoles.USER.getRole());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
        userRepository.save(userEntity);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public Boolean userIsValid(AuthRequest authRequest) {
        UserEntity userEntity = userRepository.findByUsername(authRequest.getUsername());
        if (userEntity == null) return false;
        return new BCryptPasswordEncoder().matches(authRequest.getPassword(), userEntity.getPassword());
    }

    public UserResponse getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        return UserMapper.mapEntityToResponse(userEntity, contactService.getAllContactsByUserId(userEntity.getUserId()));
    }
}
