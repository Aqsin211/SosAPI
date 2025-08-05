package az.company.msuser.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrudMessages {
    OPERATION_CREATED("Created"),
    OPERATION_UPDATED("Updated"),
    OPERATION_DELETED("Deleted");
    private final String message;
}

