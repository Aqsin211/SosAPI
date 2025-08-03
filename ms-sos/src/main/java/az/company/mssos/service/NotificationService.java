package az.company.mssos.service;

import az.company.mssos.client.UserClient;
import az.company.mssos.dao.response.ContactResponse;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.entity.LocationEntity;
import az.company.mssos.entity.SosAlert;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final TwilioRestClient twilioClient;
    private final SosAcknowledgmentService acknowledgmentService;
    private final UserClient userClient;

    public void sendFallbackAlert(SosAlert alert) {
        UserResponse user = userClient.getUser(alert.getUser().getUserId()).getBody();
        LocationEntity location = alert.getLocation();
        List<ContactResponse> contacts = Objects.requireNonNull(user).getContacts();

        contacts.forEach(contact -> {
            // Skip if already acknowledged
            if (acknowledgmentService.isAlertReceived(alert.getSosId(), contact.getUserId())) {
                return;
            }

            // SMS via Twilio (phone number required)
            if (contact.getPhoneNumber() != null && !contact.getPhoneNumber().isBlank()) {
                sendSmsAlert(contact.getPhoneNumber(), user.getUsername(), location);
            }

            // Email (gmail required)
            if (contact.getGmail() != null && !contact.getGmail().isBlank()) {
                sendEmailAlert(contact.getGmail(), user.getUsername(), location);
            }
        });
    }

    private void sendSmsAlert(String phoneNumber, String username, LocationEntity location) {
        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber("+1234567890"),
                String.format("URGENT: %s needs help! Location: %s", username, location)
        ).create();
    }

    private void sendEmailAlert(String email, String username, LocationEntity location) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("SOS Alert for " + username);
        message.setText(String.format(
                "User %s triggered an SOS. Location: %s",
                username,
                location
        ));
        mailSender.send(message);
    }
}