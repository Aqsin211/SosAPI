package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.entity.LocationEntity;
import az.company.mssos.entity.SosAlert;
import az.company.mssos.entity.UserEntity;
import az.company.mssos.mapper.UserMapper;
import az.company.mssos.repository.SosAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SosService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SosAlertRepository sosAlertRepository;
    private final UserClient userClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void triggerSos(Long userId, LocationEntity location) {
        UserEntity user = UserMapper.mapResponseToEntity(Objects.requireNonNull(userClient.getUser(userId).getBody()));

        // Set location timestamp
        location.setTimestamp(Instant.now());

        SosAlert alert = SosAlert.builder()
                .user(user)
                .location(location)
                .triggeredAt(Instant.now())
                .resolved(false)
                .build();

        // Save to PostgreSQL
        SosAlert savedAlert = sosAlertRepository.save(alert);

        // Broadcast to WebSocket
        messagingTemplate.convertAndSend(
                "/topic/sos-alerts/" + userId,
                Map.of(
                        "alertId", savedAlert.getSosId(),
                        "userId", userId,
                        "location", location,
                        "triggeredAt", savedAlert.getTriggeredAt()
                )
        );

        // Store in Redis with 3-minute TTL
        redisTemplate.opsForValue().set(
                "sos:" + userId,
                savedAlert.getSosId().toString(),
                Duration.ofMinutes(3)
        );
    }
}