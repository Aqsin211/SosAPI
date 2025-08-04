package com.example.demo.dao.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactRequest {
    @NotBlank
    String name;
    @Email
    @NotBlank
    String gmail;
    String phoneNumber;
    @JsonIgnore
    String role;
}
