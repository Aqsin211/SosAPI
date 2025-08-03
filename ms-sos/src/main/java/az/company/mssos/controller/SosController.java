package az.company.mssos.controller;

import az.company.mssos.entity.LocationEntity;
import az.company.mssos.service.LocationService;
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
    private final LocationService locationService;

    public SosController(SosService sosService, LocationService locationService) {
        this.sosService = sosService;
        this.locationService = locationService;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerSos(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody LocationEntity location
    ) {
        sosService.triggerSos(userId, location);
        return ResponseEntity.ok("SOS triggered");
    }

    @PostMapping("/location/update")
    public ResponseEntity<String> updateLocation(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody LocationEntity location) {

        locationService.updateUserLocation(userId, location);
        return ResponseEntity.ok("Location updated");
    }
}