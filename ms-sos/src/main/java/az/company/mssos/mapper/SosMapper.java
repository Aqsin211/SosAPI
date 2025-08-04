package az.company.mssos.mapper;

import az.company.mssos.dao.response.SosResponse;
import az.company.mssos.entity.SosAlert;

public enum SosMapper {
    SOS_MAPPER;

    public static SosResponse mapEntityToResponse(SosAlert sosAlert) {
        return SosResponse.builder()
                .sosId(sosAlert.getSosId())
                .triggeredAt(sosAlert.getTriggeredAt())
                .userId(sosAlert.getUser().getUserId())
                .location(sosAlert.getLocation())
                .resolvedAt(sosAlert.getResolvedAt())
                .resolved(sosAlert.isResolved())
                .build();
    }
}
