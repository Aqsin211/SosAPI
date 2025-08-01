package com.example.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {
    ENTITY_NOT_FOUND_OR_UNAUTHORIZED("Entity not found or unauthorized"),
    USER_CREDENTIALS_WRONG_OR_USER_DOES_NOT_EXIST("User credentials wrong or user does not exist"),
    TOKEN_IS_EXPIRED("Token is expired"),
    TOKEN_IS_INVALID("Token is invalid"),
    USER_EXISTS("User already exists"),
    GMAIL_AT_USE("Gmail is already at use"),
    ENTITY_DOES_NOT_EXIST("Entity does not exist"),
    USER_DOES_NOT_EXIST("User does not exist"),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED");
    private final String message;
}
