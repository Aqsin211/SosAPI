package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.model.response.UserResponse;
import io.github.cdimascio.dotenv.Dotenv;
import az.company.mssos.dao.entity.LocationEntity;
import az.company.mssos.dao.entity.SosAlert;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final UserClient userClient;
    private final String senderEmail;

    public NotificationService(JavaMailSender mailSender, UserClient userClient) {
        this.mailSender = mailSender;
        this.userClient = userClient;
        Dotenv dotenv = Dotenv.load();
        this.senderEmail = dotenv.get("EMAIL_USERNAME");
    }


    public void sendFallbackAlert(SosAlert alert) {
        UserResponse user = Objects.requireNonNull(userClient.getUser(alert.getUser().getUserId()).getBody());
        LocationEntity location = alert.getLocation();

        user.getContacts().stream()
                .filter(contact -> contact.getGmail() != null && !contact.getGmail().isBlank())
                .forEach(contact -> sendEmailAlert(contact.getGmail(), user.getUsername(), location, alert.getTriggeredAt()));
    }

    private void sendEmailAlert(String email, String username, LocationEntity location, Instant triggeredAt) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(senderEmail);
        message.setSubject("URGENT: SOS Alert for " + username);
        message.setText(String.format(
                "User %s triggered an SOS!\n\n" +
                        "Location: %s\n" +
                        "Coordinates: %.6f, %.6f\n" +
                        "Time: %s\n\n" +
                        "Immediate action required!",
                username,
                location.getAddress() != null ? location.getAddress() : "Unknown location",
                location.getLatitude(),
                location.getLongitude(),
                triggeredAt.toString()
        ));
        mailSender.send(message);
    }
}