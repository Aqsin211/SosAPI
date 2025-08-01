package az.company.msauth.controller;

import az.company.msauth.client.UserClient;
import az.company.msauth.dao.request.AuthRequest;
import az.company.msauth.dao.response.AuthResponse;
import az.company.msauth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final UserClient userClient;

    public AuthController(JwtService jwtService, UserClient userClient) {
        this.jwtService = jwtService;
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<AuthResponse> getToken(@RequestBody AuthRequest authRequest) {

        if (userClient.userValid(authRequest)) {
            String token = jwtService.generateToken(, authRequest.getUsername(), "USER");
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new RuntimeException("Unauthorized");
        }

    }
}
