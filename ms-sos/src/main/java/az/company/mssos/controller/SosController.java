package az.company.mssos.controller;

import az.company.mssos.dao.request.SosRequest;
import az.company.mssos.dao.response.SosResponse;
import az.company.mssos.entity.LocationEntity;
import az.company.mssos.service.SosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/all")
    public ResponseEntity<List<SosResponse>> getAllSosAlerts(
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(sosService.getSosAlertsByUserId(userId));
    }

    @GetMapping("/{sosId}")
    public ResponseEntity<SosResponse> getSosAlert(
            @RequestHeader("X-User-ID") Long userId, @PathVariable Long sosId) {
        return ResponseEntity.ok(sosService.getSosAlert(userId, sosId));
    }

    @DeleteMapping("/{sosId}")
    public ResponseEntity<String> deleteSosAlert(
            @RequestHeader("X-User-ID") Long userId, @PathVariable Long sosId) {
        sosService.deleteSosAlert(userId, sosId);
        return ResponseEntity.ok("DELETED");
    }

    @PutMapping("/{sosId}")
    public ResponseEntity<String> updateSosAlert(
            @RequestHeader("X-User-ID") Long userId, @PathVariable Long sosId, @RequestBody SosRequest sosRequest) {
        sosService.updateSosAlert(userId, sosId, sosRequest);
        return ResponseEntity.ok("UPDATED");
    }
}