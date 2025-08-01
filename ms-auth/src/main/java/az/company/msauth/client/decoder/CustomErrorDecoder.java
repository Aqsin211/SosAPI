package az.company.msauth.client.decoder;

import az.company.orders.exception.CustomFeignException;
import az.company.orders.util.MapperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Response;
import feign.codec.ErrorDecoder;

import static az.company.orders.client.decoder.JsonNodeFieldName.MESSAGE;
import static az.company.orders.model.enums.ErrorMessage.CLIENT_ERROR;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        String errorMessage = CLIENT_ERROR.getMessage();
        Integer statusCode = response.status();

        JsonNode jsonNode;
        try (var body = response.body().asInputStream()) {
            jsonNode = MapperUtil.MAPPER_UTIL.map(body, JsonNode.class);
        } catch (Exception exception) {
            throw new CustomFeignException(statusCode, CLIENT_ERROR.getMessage());
        }
        if (jsonNode.has(MESSAGE.getValue())) errorMessage = jsonNode.get(MESSAGE.getValue()).asText();
        return new CustomFeignException(statusCode, errorMessage);
    }
}
