package com.example.demo.service;

import com.example.demo.dao.request.ContactRequest;
import com.example.demo.dao.response.ContactResponse;
import com.example.demo.entity.ContactEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.ErrorMessages;
import com.example.demo.enums.UserRoles;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.UserExistsException;
import com.example.demo.mapper.ContactMapper;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public void createContact(ContactRequest contactRequest, Long userId) {
        if (contactRepository.existsByName(contactRequest.getName())) {
            throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage());
        }
        if (contactRepository.existsByGmail(contactRequest.getGmail())) {
            throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage());
        }
        if (contactRepository.existsByPhoneNumber(contactRequest.getPhoneNumber())) {
            throw new UserExistsException("Phone number already at use");
        }
        contactRequest.setRole(UserRoles.CONTACT.getRole());
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessages.USER_DOES_NOT_EXIST.getMessage()));
        contactRepository.save(ContactMapper.mapRequestToEntity(contactRequest, userEntity));
    }

    public ContactResponse getContactById(Long contactId, Long userId) {
        ContactResponse contactResponse = contactRepository.findById(contactId).map(ContactMapper::mapEntityToResponse).orElseThrow(
                () -> new NotFoundException("Contact does not exists"));
        if (Objects.equals(contactResponse.getUserId(), userId)) {
            return contactResponse;
        } else {
            throw new RuntimeException("Forbidden");
        }
    }

    public List<ContactResponse> getAllContacts(Long userId) {
        return contactRepository.findAll().stream()
                .filter(contactEntity -> contactEntity.getUserEntity().getUserId().equals(userId))
                .map(ContactMapper::mapEntityToResponse).toList();
    }

    public void deleteContact(Long contactId, Long userId) {
        ContactResponse contactResponse = contactRepository.findById(contactId).map(ContactMapper::mapEntityToResponse).orElseThrow(
                () -> new NotFoundException("Contact does not exists"));
        if (Objects.equals(contactResponse.getUserId(), userId)) {
            contactRepository.deleteById(contactId);
        } else {
            throw new RuntimeException("Forbidden");
        }
    }

    public void updateContact(Long contactId, ContactRequest contactRequest, Long userId) {
        ContactEntity contactEntity = contactRepository.findById(contactId).orElseThrow(
                () -> new NotFoundException(ErrorMessages.USER_DOES_NOT_EXIST.getMessage()));
        if (!Objects.equals(contactEntity.getUserEntity().getUserId(), userId)) {
            throw new RuntimeException("Forbidden");
        }
        if (userRepository.existsByUsername(contactRequest.getName()) && !Objects.equals(contactEntity.getName(), contactRequest.getName())) {
            throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage());
        } else {
            contactEntity.setName(contactRequest.getName());
        }
        if (userRepository.existsByGmail(contactRequest.getGmail()) && !Objects.equals(contactEntity.getGmail(), contactRequest.getGmail())) {
            throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage());
        } else {
            contactEntity.setGmail(contactRequest.getGmail());
        }
        if (userRepository.existsByPhoneNumber(contactRequest.getPhoneNumber()) && !Objects.equals(contactEntity.getPhoneNumber(), contactRequest.getPhoneNumber())) {
            throw new UserExistsException("Phone number already at use");
        } else {
            contactEntity.setPhoneNumber(contactRequest.getPhoneNumber());
        }
        contactRepository.save(contactEntity);
    }
}
