package az.company.apigateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key key;

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

            // ✅ Allow POST to /auth
            if (path.equals("/auth") && HttpMethod.POST.equals(method)) {
                return chain.filter(exchange);
            }

            // ✅ Allow POST to /user (register)
            if (path.equals("/user") && HttpMethod.POST.equals(method)) {
                return chain.filter(exchange);
            }

            // 🔒 All other endpoints require JWT
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
