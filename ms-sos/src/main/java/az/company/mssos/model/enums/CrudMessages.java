package az.company.mssos.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrudMessages {
    OPERATION_UPDATED("Updated"),
    OPERATION_DELETED("Deleted");
    private final String message;
}

