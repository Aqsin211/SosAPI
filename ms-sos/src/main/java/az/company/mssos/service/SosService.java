package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.entity.LocationEntity;
import az.company.mssos.entity.SosAlert;
import az.company.mssos.entity.UserEntity;
import az.company.mssos.mapper.UserMapper;
import az.company.mssos.repository.SosAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SosService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SosAlertRepository sosAlertRepository;
    private final LocationService locationService;
    private final UserClient userClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;

    // Unified location handling
    public void handleLocationUpdate(Long userId, LocationEntity location) {
        // Store with updated timestamp
        location.setTimestamp(Instant.now());
        locationService.updateUserLocation(userId, location);

        // Auto-check inactivity
        if (isUserInactive(userId, location)) {
            triggerSos(userId, location);
        }
    }

    private boolean isUserInactive(Long userId, LocationEntity location) {
        return location.getTimestamp()
                .isBefore(Instant.now().minus(15, ChronoUnit.MINUTES));
    }

    @Transactional
    public void triggerSos(Long userId, LocationEntity location) {
        UserEntity user = UserMapper.mapResponseToEntity(
                Objects.requireNonNull(userClient.getUser(userId).getBody())
        );
        location.setTimestamp(Instant.now());

        SosAlert alert = SosAlert.builder()
                .user(user)
                .location(location)
                .triggeredAt(Instant.now())
                .resolved(false)
                .build();

        SosAlert savedAlert = sosAlertRepository.save(alert);

        // Real-time alert
        messagingTemplate.convertAndSend(
                "/topic/sos-alerts/" + userId,
                Map.of(
                        "alertId", savedAlert.getSosId(),
                        "userId", userId,
                        "location", location,
                        "triggeredAt", savedAlert.getTriggeredAt()
                )
        );

        // Redis session with 3-minute TTL
        redisTemplate.opsForValue().set(
                "sos:" + userId,
                savedAlert.getSosId().toString(),
                Duration.ofMinutes(3)
        );
    }

    @Transactional
    public void resolveSos(Long alertId) {
        sosAlertRepository.findById(alertId).ifPresent(alert -> {
            alert.setResolved(true);
            alert.setResolvedAt(Instant.now());
            sosAlertRepository.save(alert);

            messagingTemplate.convertAndSend(
                    "/topic/sos-alerts/" + alert.getUser().getUserId(),
                    Map.of(
                            "alertId", alertId,
                            "status", "RESOLVED"
                    )
            );
            redisTemplate.delete("sos:" + alert.getUser().getUserId());
        });
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void checkInactiveUsers() {
        Set<String> locationKeys = locationService.getAllLocationKeys();

        locationKeys.forEach(key -> {
            Long userId = Long.parseLong(key.split(":")[2]);
            LocationEntity location = locationService.getLastKnownLocation(userId);

            if (isUserInactive(userId, location) &&
                    !redisTemplate.hasKey("sos:" + userId)) {
                triggerSos(userId, location);
            }
        });
    }
}