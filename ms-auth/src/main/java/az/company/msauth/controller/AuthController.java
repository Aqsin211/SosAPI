package az.company.msauth.controller;

import az.company.msauth.client.UserClient;
import az.company.msauth.dao.request.AuthRequest;
import az.company.msauth.dao.response.AuthResponse;
import az.company.msauth.dao.response.UserResponse;
import az.company.msauth.enums.ErrorMessages;
import az.company.msauth.exception.UnauthorizedException;
import az.company.msauth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<AuthResponse> getToken(@RequestBody AuthRequest authRequest) {

        if (userClient.userValid(authRequest)) {
            UserResponse userResponse = userClient.getUserByUsername(authRequest.getUsername()).getBody();
            String token = jwtService.generateToken(userResponse.getUserId(), userResponse.getUsername(), userResponse.getRole());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_ACCESS.getMessage());
        }

    }
}
