package com.phamquocviet.apigateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class AuthServiceClient {
    private final WebClient webClient;

    public AuthServiceClient(@Value("${auth-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Mono<Boolean> validate(String key, String scope) {
        return webClient.get()
                .uri(uri -> uri.path("/internal/api-keys/validate").queryParam("key", key)
                        .queryParam("scope", scope).build())
                .retrieve().bodyToMono(ValidationResponse.class).map(ValidationResponse::valid)
                .timeout(Duration.ofSeconds(3));
    }

    private record ValidationResponse(boolean valid) {}
}
