package az.company.mssos.jwt;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = request.getHeaders().getFirst("X-User-ID");
        if (userId == null) throw new IllegalArgumentException("Missing X-User-ID");
        return () -> userId;
    }
}