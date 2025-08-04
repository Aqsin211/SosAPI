package az.company.mssos.dao.request;

import az.company.mssos.entity.LocationEntity;
import az.company.mssos.exception.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SosRequest {
    @NotNull(message = ValidationMessages.LOCATION_MUST_NOT_BE_NULL)
    LocationEntity location;
    @PastOrPresent(message = ValidationMessages.RESOLVING_TIME_MUST_BE_IN_THE_PAST_OR_NOW)
    Instant resolvedAt;
    boolean resolved;
}
