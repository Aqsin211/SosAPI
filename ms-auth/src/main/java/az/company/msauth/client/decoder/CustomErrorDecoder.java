package az.company.msauth.client.decoder;

import az.company.msauth.util.MapperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Response;
import feign.codec.ErrorDecoder;

import static az.company.msauth.client.decoder.JsonNodeFieldName.MESSAGE;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        String errorMessage = "Client error";
        JsonNode jsonNode;
        try (var body = response.body().asInputStream()) {
            jsonNode = MapperUtil.MAPPER_UTIL.map(body, JsonNode.class);
        } catch (Exception exception) {
            throw new RuntimeException(errorMessage);
        }
        if (jsonNode.has(MESSAGE.getValue())) errorMessage = jsonNode.get(MESSAGE.getValue()).asText();
        return new RuntimeException(errorMessage);
    }
}
