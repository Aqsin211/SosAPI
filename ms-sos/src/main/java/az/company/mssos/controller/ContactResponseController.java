package az.company.mssos.controller;

import az.company.mssos.dao.response.SosResponse;
import az.company.mssos.model.enums.ResponseMessages;
import az.company.mssos.service.SosAcknowledgmentService;
import az.company.mssos.service.SosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sos-response")
@RequiredArgsConstructor
public class ContactResponseController {
    private final SosAcknowledgmentService acknowledgmentService;
    private final SosService sosService;

    @PostMapping("/all")
    public ResponseEntity<List<SosResponse>> getSosAlerts(@RequestHeader("X-contact-ID") Long contactId) {
        return ResponseEntity.ok(sosService.getSosAlerts(contactId));
    }

    @PostMapping("/{sosId}")
    public ResponseEntity<SosResponse> getSosAlertForContact(@RequestHeader("X-contact-ID") Long contactId, @PathVariable Long sosId) {
        return ResponseEntity.ok(sosService.getSosAlertForContact(contactId, sosId));
    }

    // For contacts to acknowledge alerts
    @PostMapping("/acknowledge/{sosId}")
    public ResponseEntity<String> acknowledgeAlert(
            @RequestHeader("X-contact-ID") Long contactId,
            @PathVariable Long sosId) {
        acknowledgmentService.markAlertReceived(sosId, contactId);
        return ResponseEntity.ok(ResponseMessages.ALERT_ACKNOWLEDGED.getMessage());
    }

    // For contacts to resolve SOS
    @PostMapping("/resolve/{alertId}")
    public ResponseEntity<String> resolveSos(
            @RequestHeader("X-contact-ID") Long contactId,
            @PathVariable Long alertId) {
        sosService.resolveSos(alertId, contactId);
        return ResponseEntity.ok(ResponseMessages.SOS_RESOLVED.getMessage());
    }
}