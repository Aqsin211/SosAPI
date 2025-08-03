package az.company.mssos.listener;

import az.company.mssos.client.UserClient;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.entity.SosAlert;
import az.company.mssos.mapper.UserMapper;
import az.company.mssos.service.LocationService;
import az.company.mssos.service.NotificationService;
import org.springframework.stereotype.Component;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Objects;

@Component
public class SosExpirationListener implements MessageListener {
    private final NotificationService notificationService;
    private final LocationService locationService;
    private final UserClient userClient;

    public SosExpirationListener(NotificationService notificationService, LocationService locationService, UserClient userClient) {
        this.notificationService = notificationService;
        this.locationService = locationService;
        this.userClient = userClient;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        if (expiredKey.startsWith("sos:")) {
            Long userId = Long.parseLong(expiredKey.split(":")[1]);
            UserResponse user = userClient.getUser(userId).getBody();
            SosAlert sosAlert = new SosAlert();
            sosAlert.builder()
                    .location(locationService.getLastKnownLocation(userId))
                    .user(UserMapper.mapResponseToEntity(Objects.requireNonNull(user)))
                    .build();
            notificationService.sendFallbackAlert(sosAlert);
        }
    }
}