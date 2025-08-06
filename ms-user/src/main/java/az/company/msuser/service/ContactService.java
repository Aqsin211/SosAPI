package az.company.msuser.service;

import az.company.msuser.model.request.ContactRequest;
import az.company.msuser.model.response.ContactResponse;
import az.company.msuser.dao.entity.ContactEntity;
import az.company.msuser.dao.entity.UserEntity;
import az.company.msuser.model.enums.ErrorMessages;
import az.company.msuser.model.enums.UserRoles;
import az.company.msuser.exception.NotFoundException;
import az.company.msuser.exception.UnauthorizedException;
import az.company.msuser.exception.UserExistsException;
import az.company.msuser.model.mapper.ContactMapper;
import az.company.msuser.dao.repository.ContactRepository;
import az.company.msuser.dao.repository.UserRepository;
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
            throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage());
        }
        contactRequest.setRole(UserRoles.CONTACT.getRole());
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessages.USER_DOES_NOT_EXIST.getMessage()));
        contactRepository.save(ContactMapper.mapRequestToEntity(contactRequest, userEntity));
    }

    public ContactResponse getContactById(Long contactId, Long userId) {
        ContactResponse contactResponse = contactRepository.findById(contactId).map(ContactMapper::mapEntityToResponse).orElseThrow(
                () -> new NotFoundException(ErrorMessages.CONTACT_DOES_NOT_EXIST.getMessage()));
        if (Objects.equals(contactResponse.getUserId(), userId)) {
            return contactResponse;
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_ACCESS.getMessage());
        }
    }

    public List<ContactResponse> getAllContactsByUserId(Long userId) {
        return contactRepository.findAll().stream()
                .filter(contactEntity -> contactEntity.getUserEntity().getUserId().equals(userId))
                .map(ContactMapper::mapEntityToResponse).toList();
    }

    public void deleteContact(Long contactId, Long userId) {
        ContactResponse contactResponse = contactRepository.findById(contactId).map(ContactMapper::mapEntityToResponse).orElseThrow(
                () -> new NotFoundException(ErrorMessages.CONTACT_DOES_NOT_EXIST.getMessage()));
        if (Objects.equals(contactResponse.getUserId(), userId)) {
            contactRepository.deleteById(contactId);
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_ACCESS.getMessage());
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
            throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage());
        } else {
            contactEntity.setPhoneNumber(contactRequest.getPhoneNumber());
        }
        contactRepository.save(contactEntity);
    }

    public List<ContactResponse> getAllContacts() {
        return contactRepository.findAll().stream().map(ContactMapper::mapEntityToResponse).toList();
    }
}
