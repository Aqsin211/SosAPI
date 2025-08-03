package az.company.mssos.controller;

import az.company.mssos.service.SosAcknowledgmentService;
import az.company.mssos.service.SosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sos-response")
@RequiredArgsConstructor
public class ContactResponseController {
    private final SosAcknowledgmentService acknowledgmentService;
    private final SosService sosService;

    // For contacts to acknowledge alerts
    @PostMapping("/acknowledge/{sosId}")
    public ResponseEntity<String> acknowledgeAlert(
            @RequestHeader("X-User-ID") Long contactId,
            @PathVariable Long sosId) {
        acknowledgmentService.markAlertReceived(sosId, contactId);
        return ResponseEntity.ok("Alert acknowledged");
    }

    // For contacts to resolve SOS
    @PostMapping("/resolve/{alertId}")
    public ResponseEntity<String> resolveSos(
            @RequestHeader("X-User-ID") Long contactId,
            @PathVariable Long alertId) {
        sosService.resolveSos(alertId,contactId);
        return ResponseEntity.ok("SOS resolved by contact");
    }
}