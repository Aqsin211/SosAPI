package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.dao.request.SosRequest;
import az.company.mssos.dao.response.ContactResponse;
import az.company.mssos.dao.response.SosResponse;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.dao.entity.LocationEntity;
import az.company.mssos.dao.entity.SosAlert;
import az.company.mssos.dao.entity.UserEntity;
import az.company.mssos.model.enums.ErrorMessages;
import az.company.mssos.exception.ActionException;
import az.company.mssos.exception.NotFoundException;
import az.company.mssos.exception.UnauthorizedException;
import az.company.mssos.mapper.SosMapper;
import az.company.mssos.mapper.UserMapper;
import az.company.mssos.dao.repository.SosAlertRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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
                throw new UnauthorizedException(ErrorMessages.CONTACT_UNAUTHORIZED.getMessage());
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
                // Check if inactivity SOS was already triggered
                String inactivityKey = "sos-inactivity:" + userId;
                Boolean alreadyTriggered = redisTemplate.hasKey(inactivityKey);
                if (!alreadyTriggered) {
                    triggerSos(userId, location);
                    // Set flag with TTL to avoid repeated SOS triggers
                    redisTemplate.opsForValue().set(inactivityKey, "1", Duration.ofMinutes(30));
                }
            }
        });
    }


    public SosAlert getSosAlertById(Long sosId) {
        return sosAlertRepository.findById(sosId).orElseThrow(() -> new NotFoundException(ErrorMessages.SOS_ALERT_NOT_FOUND.getMessage()));
    }

    public List<SosResponse> getSosAlerts(Long contactId) {
        List<ContactResponse> contactResponses = userClient.getAllContacts().getBody();
        if (contactResponses == null) {
            throw new ActionException(ErrorMessages.FAILED_TO_FETCH_CONTACTS.getMessage());
        }

        ContactResponse contactResponse = contactResponses.stream()
                .filter(contact -> contact.getContactId().equals(contactId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND.getMessage(), contactId)));

        return sosAlertRepository.findByUserUserId(contactResponse.getUserId()).stream()
                .map(SosMapper::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    public List<SosResponse> getSosAlertsByUserId(Long userId) {
        return sosAlertRepository.findByUserUserId(userId).stream()
                .map(SosMapper::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    public SosResponse getSosAlert(Long userId, Long sosId) {
        SosAlert sosAlert = sosAlertRepository.findById(sosId).orElseThrow(() -> new NotFoundException(ErrorMessages.SOS_ALERT_NOT_FOUND.getMessage()));
        if (sosAlert.getUser().getUserId().equals(userId)) {
            return SosMapper.mapEntityToResponse(sosAlert);
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_FOR_THIS_ACTION.getMessage());
        }
    }

    public void deleteSosAlert(Long userId, Long sosId) {
        SosAlert sosAlert = sosAlertRepository.findById(sosId).orElseThrow(() -> new NotFoundException(ErrorMessages.SOS_ALERT_NOT_FOUND.getMessage()));
        if (sosAlert.getUser().getUserId().equals(userId)) {
            sosAlertRepository.delete(sosAlert);
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_FOR_THIS_ACTION.getMessage());
        }
    }

    public void updateSosAlert(Long userId, Long sosId, SosRequest sosRequest) {
        SosAlert sosAlert = sosAlertRepository.findById(sosId).orElseThrow(() -> new NotFoundException(ErrorMessages.SOS_ALERT_NOT_FOUND.getMessage()));
        if (sosAlert.getUser().getUserId().equals(userId)) {
            sosAlert.setLocation(sosRequest.getLocation());
            sosAlert.setResolved(sosRequest.isResolved());
            sosAlert.setResolvedAt(sosRequest.getResolvedAt());
            sosAlertRepository.save(sosAlert);
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_FOR_THIS_ACTION.getMessage());
        }
    }

    public SosResponse getSosAlertForContact(Long contactId, Long sosId) {
        List<ContactResponse> contactResponses = userClient.getAllContacts().getBody();
        if (contactResponses == null) {
            throw new ActionException(ErrorMessages.FAILED_TO_FETCH_CONTACTS.getMessage());
        }

        ContactResponse contactResponse = contactResponses.stream()
                .filter(contact -> contact.getContactId().equals(contactId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CONTACT_NOT_FOUND.getMessage(), contactId)));

        return sosAlertRepository.findByUserUserId(contactResponse.getUserId()).stream()
                .map(SosMapper::mapEntityToResponse)
                .filter(sos -> sos.getSosId().equals(sosId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.SOS_ALERT_NOT_FOUND.getMessage(), sosId)));
    }

}