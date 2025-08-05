package az.company.mssos.listener;

import az.company.mssos.dao.entity.SosAlert;
import az.company.mssos.dao.repository.SosAlertRepository;
import az.company.mssos.service.NotificationService;
import az.company.mssos.service.SosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SosExpirationListener implements MessageListener {
    private final NotificationService notificationService;
    private final SosService sosService;
    private final SosAlertRepository sosAlertRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String expiredKey = new String(message.getBody());
            if (!expiredKey.startsWith("sos:")) return;

            Long userId = Long.parseLong(expiredKey.split(":")[1]);
            String sosIdString = redisTemplate.opsForValue().get(expiredKey);

            if (sosIdString == null) {
                log.warn("SOS expired but no alert ID found for user: {}", userId);
                return;
            }

            SosAlert alert = sosService.getSosAlertById(Long.parseLong(sosIdString));
            if (alert == null) {
                log.error("Missing SOS alert record for ID: {}", sosIdString);
                return;
            }

            if (!alert.isResolved()) {
                log.info("Triggering fallback for unresolved SOS (User: {}, Alert: {})", userId, alert.getSosId());
                notificationService.sendFallbackAlert(alert);

                alert.setResolved(true);
                sosAlertRepository.save(alert);
            }
        } catch (Exception e) {
            log.error("Failed to process Redis expiration event", e);
        }
    }
}