package az.company.msauth.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {
    SERVER_ERROR("Unexpected error occurred"),
    UNAUTHORIZED_ACCESS("Username or password invalid");
    private final String message;
}
