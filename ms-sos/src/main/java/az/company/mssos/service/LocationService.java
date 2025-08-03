package az.company.mssos.service;

import az.company.mssos.entity.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final RedisTemplate<String, LocationEntity> redisTemplate;

    public void updateUserLocation(Long userId, LocationEntity location) {
        location.setTimestamp(Instant.now());
        redisTemplate.opsForValue().set(
                "user:location:" + userId,
                location,
                Duration.ofMinutes(20)
        );
    }

    public Set<String> getAllLocationKeys() {
        return redisTemplate.keys("user:location:*");
    }

    public LocationEntity getLastKnownLocation(Long userId) {
        return redisTemplate.opsForValue().get("user:location:" + userId);
    }
}