package az.company.mssos.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SosAcknowledgmentService {
    private final RedisTemplate<String, Boolean> redisTemplate;

    public SosAcknowledgmentService(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markAlertReceived(Long contactId, Long sosId) {
        redisTemplate.opsForValue().set(
                "sos-ack:%s:%s".formatted(sosId, contactId),
                true,
                Duration.ofMinutes(3)
        );
    }

    public boolean isAlertReceived(Long contactId, Long sosId) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().get("sos-ack:%s:%s".formatted(sosId, contactId))
        );
    }
}