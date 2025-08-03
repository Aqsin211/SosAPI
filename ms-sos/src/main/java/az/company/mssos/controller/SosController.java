package az.company.mssos.controller;

import az.company.mssos.entity.LocationEntity;
import az.company.mssos.service.SosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody LocationEntity location) {
        sosService.triggerSos(userId, location);
        return ResponseEntity.ok("SOS triggered");
    }

    @PostMapping("/location/update")
    public ResponseEntity<String> updateLocation(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody LocationEntity location) {
        sosService.handleLocationUpdate(userId, location);
        return ResponseEntity.ok("Location processed");
    }
}