package az.company.msauth.client;

import az.company.msauth.client.decoder.CustomErrorDecoder;
import az.company.msauth.config.FeignAuthInterceptor;
import az.company.msauth.dao.request.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ms-user",
        url = "http://localhost:8082/user",
        configuration = {CustomErrorDecoder.class}
)
public interface UserClient {
    @GetMapping("/validation")
    Boolean userValid(@RequestBody AuthRequest authRequest);
}


//@FeignClient(
//        name = "ms-product",
//        url = "http://ms-product:8080/v1/products",
//        configuration = {FeignAuthInterceptor.class, CustomErrorDecoder.class}
//)
//public interface ProductClient {
//    @PostMapping("/reduce-quantity")
//    void reduceQuantity(@RequestBody ReduceQuantityRequest reduceQuantityRequest);
//
//
//}
