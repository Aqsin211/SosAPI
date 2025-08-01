package az.company.mssos.controller;

import az.company.mssos.entity.LocationEntity;
import az.company.mssos.service.SosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sos")
public class SosController {
    private final SosService sosService;

    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerSos(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody LocationEntity location
    ) {
        sosService.triggerSos(userId, location);
        return ResponseEntity.ok("SOS triggered");
    }
}