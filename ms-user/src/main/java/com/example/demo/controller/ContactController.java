package com.example.demo.controller;

import com.example.demo.dao.request.ContactRequest;
import com.example.demo.dao.response.ContactResponse;
import com.example.demo.enums.CrudMessages;
import com.example.demo.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createContact(@RequestBody ContactRequest contactRequest, @RequestHeader("X-User-ID") Long userId) {
        contactService.createContact(contactRequest, userId);
        return CrudMessages.OPERATION_CREATED.getMessage();
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponse> getContact(@PathVariable Long contactId, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(contactService.getContactById(contactId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactResponse>> getAllContacts(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(contactService.getAllContactsByUserId(userId));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<String> deleteContact(@PathVariable Long contactId, @RequestHeader("X-User-ID") Long userId) {
        contactService.deleteContact(contactId, userId);
        return ResponseEntity.ok(CrudMessages.OPERATION_DELETED.getMessage());
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<String> updateContact(@PathVariable Long contactId, @RequestBody ContactRequest contactRequest, @RequestHeader("X-User-ID") Long userId) {
        contactService.updateContact(contactId, contactRequest, userId);
        return ResponseEntity.ok(CrudMessages.OPERATION_UPDATED.getMessage());
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }
}
