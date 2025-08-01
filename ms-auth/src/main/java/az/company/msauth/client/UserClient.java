package az.company.msauth.client;

import az.company.msauth.client.decoder.CustomErrorDecoder;
import az.company.msauth.dao.request.AuthRequest;
import az.company.msauth.dao.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "ms-user",
        url = "http://localhost:8082/user",
        configuration = {CustomErrorDecoder.class}
)
public interface UserClient {
    @GetMapping("/validation")
    Boolean userValid(@RequestBody AuthRequest authRequest);

    @GetMapping
    ResponseEntity<UserResponse> getUserByUsername(@RequestHeader("X-User-name") String username);
}