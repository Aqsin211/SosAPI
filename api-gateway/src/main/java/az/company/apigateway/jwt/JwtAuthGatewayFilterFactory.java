package az.company.apigateway.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key key;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            HttpMethod method = exchange.getRequest().getMethod();

            if ((path.equals("/auth") || path.equals("/user")) && HttpMethod.POST.equals(method)) {
                return chain.filter(exchange);
            }

            // Special logic for contact-based POST requests
            if (path.startsWith("/sos-response") && HttpMethod.POST.equals(method)) {
                final ServerWebExchange originalExchange = exchange;

                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            try {
                                JsonNode jsonNode = objectMapper.readTree(bytes);
                                Long contactId = jsonNode.has("contactId") ? jsonNode.get("contactId").asLong() : null;

                                if (contactId != null) {
                                    ServerWebExchange mutatedExchange = originalExchange.mutate()
                                            .request(originalExchange.getRequest().mutate()
                                                    .header("X-Contact-ID", String.valueOf(contactId))
                                                    .build())
                                            .build();

                                    DataBuffer newBody = new DefaultDataBufferFactory().wrap(bytes);
                                    return chain.filter(mutatedExchange.mutate()
                                            .request(new ServerHttpRequestDecorator(mutatedExchange.getRequest()) {
                                                @Override
                                                public Flux<DataBuffer> getBody() {
                                                    return Flux.just(newBody);
                                                }
                                            }).build());
                                } else {
                                    originalExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                                    return originalExchange.getResponse().setComplete();
                                }

                            } catch (Exception e) {
                                originalExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                                return originalExchange.getResponse().setComplete();
                            }
                        });
            }

            //require JWT token
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                String token = authHeader.substring(7);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                exchange = exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header("X-User-ID", String.valueOf(claims.get("userId")))
                                .header("X-Username", String.valueOf(claims.get("username")))
                                .header("X-Role", String.valueOf(claims.get("role")))
                                .header("X-Internal-Gateway", "true")
                                .build()
                ).build();

                return chain.filter(exchange);

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
    }
}
