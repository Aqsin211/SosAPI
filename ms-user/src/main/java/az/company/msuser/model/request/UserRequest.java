package az.company.msuser.model.request;

import az.company.msuser.exception.ValidationMessages;
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
public class UserRequest {
    @NotBlank(message = ValidationMessages.USERNAME_CANNOT_BE_BLANK)
    String username;
    @NotBlank(message = ValidationMessages.PASSWORD_CANNOT_BE_BLANK)
    String password;
    String phoneNumber;
    @Email
    @NotBlank(message = ValidationMessages.GMAIL_CANNOT_BE_BLANK)
    String gmail;
    @JsonIgnore
    String role;
}
