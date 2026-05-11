package com.Ulises.BlackJackAPI.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements WebFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_SIZE_MS = 60_000;

    private final Map<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientId = getClientId(exchange);
        RateLimitEntry entry = rateLimitMap.compute(clientId, (key, existing) -> {
            if (existing == null || isWindowExpired(existing)) {
                return new RateLimitEntry();
            }
            return existing;
        });

        if (entry.increment() > MAX_REQUESTS_PER_MINUTE) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String getClientId(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            int tokenLength = Math.min(auth.length() - 7, 20);
            return "token:" + auth.substring(7, 7 + tokenLength);
        }
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return "ip:" + remoteAddress.getAddress().getHostAddress();
        }
        return "ip:unknown";
    }

    private boolean isWindowExpired(RateLimitEntry entry) {
        return Instant.now().toEpochMilli() - entry.windowStart > WINDOW_SIZE_MS;
    }

    private static class RateLimitEntry {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = Instant.now().toEpochMilli();

        public int increment() {
            long now = Instant.now().toEpochMilli();
            if (now - windowStart > WINDOW_SIZE_MS) {
                windowStart = now;
                count.set(1);
            } else {
                return count.incrementAndGet();
            }
            return count.get();
        }
    }
}