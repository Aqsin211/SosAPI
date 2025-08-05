package az.company.mssos.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {
    CONTACT_UNAUTHORIZED("Contact not authorized to resolve this alert"),
    UNAUTHORIZED_FOR_THIS_ACTION("Unauthorized for this action"),
    FAILED_TO_FETCH_CONTACTS("Failed to fetch contacts"),
    SOS_ALERT_NOT_FOUND("SOS alert not found with ID: %s for this contact"),
    CONTACT_NOT_FOUND("Contact not found with id: %s"),
    SERVER_ERROR("Unexpected error occurred");
    private final String message;
}
