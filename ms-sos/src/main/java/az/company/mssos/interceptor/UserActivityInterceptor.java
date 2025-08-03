package az.company.mssos.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserActivityInterceptor implements ChannelInterceptor {
    private final Map<Long, Instant> lastActivityMap = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            Long userId = Long.parseLong(accessor.getFirstNativeHeader("X-User-ID"));
            lastActivityMap.put(userId, Instant.now());
        }
        return message;
    }

    public boolean isUserActive(Long userId) {
        Instant lastActive = lastActivityMap.get(userId);
        return lastActive != null &&
                lastActive.isAfter(Instant.now().minus(15, ChronoUnit.MINUTES));
    }
}