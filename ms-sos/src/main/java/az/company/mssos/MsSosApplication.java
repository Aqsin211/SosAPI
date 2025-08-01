package az.company.mssos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSosApplication.class, args);
    }

}
