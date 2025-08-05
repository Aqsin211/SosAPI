package az.company.msuser.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {
    USER("User"),
    CONTACT("Contact");
    private final String role;
}
