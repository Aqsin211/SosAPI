package az.company.msuser.controller;

import az.company.msuser.model.request.AuthRequest;
import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;
import az.company.msuser.model.enums.CrudMessages;
import az.company.msuser.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return CrudMessages.OPERATION_CREATED.getMessage();
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/name")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestHeader("X-User-name") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestHeader("X-User-ID") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(CrudMessages.OPERATION_DELETED.getMessage());
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestHeader("X-User-ID") Long userId, @RequestBody UserRequest userRequest) {
        userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(CrudMessages.OPERATION_UPDATED.getMessage());
    }

    @PostMapping    ("/validation")
    public Boolean userValid(@RequestBody AuthRequest authRequest) {
        return userService.userIsValid(authRequest);
    }

}
