package com.example.demo.dao.request;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactRequest {
    String name;
    @Email
    String gmail;
    String phoneNumber;
    String role;
}
