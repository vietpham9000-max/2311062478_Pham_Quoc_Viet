package com.phamquocviet.apigateway.filter;

import com.phamquocviet.apigateway.client.AuthServiceClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApiKeyValidationCache {
    static final Duration TTL = Duration.ofSeconds(30);
    private final ConcurrentHashMap<CacheKey, CacheEntry> entries = new ConcurrentHashMap<>();
    private final AuthServiceClient client;

    public ApiKeyValidationCache(AuthServiceClient client) { this.client = client; }

    public Mono<Boolean> validate(String apiKey, String requiredScope) {
        CacheKey key = new CacheKey(apiKey, requiredScope);
        Instant now = Instant.now();
        CacheEntry cached = entries.get(key);
        if (cached != null && cached.expiresAt().isAfter(now)) return Mono.just(cached.valid());
        if (cached != null) entries.remove(key, cached);
        return client.validate(apiKey, requiredScope).onErrorReturn(false)
                .doOnNext(valid -> entries.put(key, new CacheEntry(valid, Instant.now().plus(TTL))));
    }

    record CacheKey(String apiKey, String requiredScope) {}
    record CacheEntry(boolean valid, Instant expiresAt) {}
}
