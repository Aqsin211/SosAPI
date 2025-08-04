package az.company.mssos.dao.response;

import az.company.mssos.entity.LocationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SosResponse {
    Long sosId;
    LocationEntity location;
    Instant triggeredAt;
    Instant resolvedAt;
    boolean resolved;
    Long userId;
}
