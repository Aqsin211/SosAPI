package az.company.msuser.model.request;

import az.company.msuser.exception.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequest {
    @NotBlank(message = ValidationMessages.USERNAME_CANNOT_BE_BLANK)
    private String username;
    @NotBlank(message = ValidationMessages.PASSWORD_CANNOT_BE_BLANK)
    private String password;
}

