package az.company.msuser.model.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactResponse {
    Long contactId;
    Long userId;
    String name;
    String gmail;
    String phoneNumber;
    String role;
}
