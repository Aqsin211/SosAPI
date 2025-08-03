package az.company.mssos.controller;

import az.company.mssos.service.SosAcknowledgmentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sos-response")
public class ContactResponseController {
    private final SosAcknowledgmentService sosService;

    public ContactResponseController(SosAcknowledgmentService sosService) {
        this.sosService = sosService;
    }

    @PostMapping("/acknowledge/{sosId}")
    public void acknowledgeAlert(
            @RequestHeader("X-User-ID") Long contactId,
            @PathVariable Long sosId
    ) {
        sosService.markAlertReceived(sosId, contactId);
    }
}