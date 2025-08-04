package az.company.mssos.exception;

import lombok.Getter;

@Getter
public class CustomFeignException extends RuntimeException {
    private final Integer statusCode;
    public CustomFeignException(Integer statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
