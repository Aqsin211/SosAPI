package az.company.mssos.dao.request;

import az.company.mssos.entity.LocationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SosRequest {
    LocationEntity location;
    Instant resolvedAt;
    boolean resolved;
}
