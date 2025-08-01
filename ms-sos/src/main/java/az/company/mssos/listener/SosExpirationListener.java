package az.company.mssos.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SosExpirationListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleRedisExpiry(KeyExpirationEvent event) {
        if (event.getKey().startsWith("sos:")) {
            Long userId = Long.parseLong(event.getKey().split(":")[1]);
            notificationService.escalateAlert(userId);
        }
    }
}