package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.entity.LocationEntity;
import az.company.mssos.entity.SosAlert;
import az.company.mssos.entity.UserEntity;
import az.company.mssos.exception.NotFoundException;
import az.company.mssos.mapper.UserMapper;
import az.company.mssos.repository.SosAlertRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;


@Service
public class SosService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SosAlertRepository sosAlertRepository;
    private final LocationService locationService;
    private final UserClient userClient;
    private final RedisTemplate<String, String> redisTemplate;

    public SosService(SimpMessagingTemplate messagingTemplate, SosAlertRepository sosAlertRepository, LocationService locationService, UserClient userClient, RedisTemplate<String, String> redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.sosAlertRepository = sosAlertRepository;
        this.locationService = locationService;
        this.userClient = userClient;
        this.redisTemplate = redisTemplate;
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

        // 1. Original alert
        messagingTemplate.convertAndSend(
                "/topic/sos-alerts/" + userId,
                Map.of(
                        "alertId", savedAlert.getSosId(),
                        "userId", userId,
                        "location", location,
                        "triggeredAt", savedAlert.getTriggeredAt()
                )
        );

        // 2. Start Redis session
        redisTemplate.opsForValue().set(
                "sos:" + userId,
                savedAlert.getSosId().toString(),
                Duration.ofMinutes(3)
        );
    }

    public void handleLocationUpdate(Long userId, LocationEntity location) {
        // 1. Always store
        locationService.updateUserLocation(userId, location);

        // 2. Stream only during active SOS
        if (redisTemplate.hasKey("sos:" + userId)) {
            messagingTemplate.convertAndSend(
                    "/topic/live-locations/" + userId,
                    Map.of(
                            "lat", location.getLatitude(),
                            "lng", location.getLongitude(),
                            "timestamp", Instant.now().toString()
                    )
            );
        }
    }

    @Transactional
    public void resolveSos(Long alertId, Long contactId) {
        sosAlertRepository.findById(alertId).ifPresent(alert -> {
            UserResponse user = userClient.getUser(alert.getUser().getUserId()).getBody();
            boolean isAuthorizedContact = user.getContacts().stream()
                    .anyMatch(contact -> contact.getContactId().equals(contactId));

            if (!isAuthorizedContact) {
                throw new SecurityException("Contact not authorized to resolve this alert");
            }

            alert.setResolved(true);
            alert.setResolvedAt(Instant.now());
            sosAlertRepository.save(alert);

            redisTemplate.delete("sos:" + alert.getUser().getUserId());

            messagingTemplate.convertAndSend(
                    "/topic/sos-alerts/" + alert.getUser().getUserId(),
                    Map.of(
                            "alertId", alertId,
                            "status", "RESOLVED",
                            "resolvedBy", contactId,
                            "timestamp", Instant.now()
                    )
            );
        });
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void checkInactiveUsers() {
        locationService.getAllLocationKeys().forEach(key -> {
            Long userId = Long.parseLong(key.split(":")[2]);
            LocationEntity location = locationService.getLastKnownLocation(userId);
            if (location.getTimestamp().isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))) {
                triggerSos(userId, location);
            }
        });
    }

    public SosAlert getSosAlertById(Long sosId) {
        return sosAlertRepository.findById(sosId).orElseThrow(() -> new NotFoundException("Sos alert not found"));
    }
}