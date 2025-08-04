package az.company.mssos.client;

import az.company.mssos.dao.response.ContactResponse;
import az.company.mssos.dao.response.UserResponse;
import az.company.mssos.client.decoder.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "ms-user",
        url = "http://localhost:8082/user",
        configuration = {CustomErrorDecoder.class}
)
public interface UserClient {
    @GetMapping
    ResponseEntity<UserResponse> getUser(@RequestHeader("X-User-ID") Long userId);

    @GetMapping("/contacts")
    ResponseEntity<List<ContactResponse>> getAllContacts();
}