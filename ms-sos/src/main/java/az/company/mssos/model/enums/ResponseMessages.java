package az.company.mssos.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessages {
    SOS_TRIGGERED("Sos triggered"),
    LOCATION_PROCESSED("Location processed"),
    ALERT_ACKNOWLEDGED("Alert acknowledged"),
    SOS_RESOLVED("Sos resolved by a contact");
    private final String message;
}
