package com.phamquocviet.apigateway.filter;

import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Component
public class ApiKeyFilter implements GlobalFilter, Ordered {
    static final String REQUIRED_SCOPE = "COURSE_READ";
    private final ApiKeyValidationCache validationCache;

    public ApiKeyFilter(ApiKeyValidationCache validationCache) { this.validationCache = validationCache; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (exchange.getRequest().getMethod() != HttpMethod.GET || !path.startsWith("/api/public/courses")) {
            return chain.filter(exchange);
        }
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || apiKey.isBlank()) return forbidden(exchange);
        return validationCache.validate(apiKey, REQUIRED_SCOPE)
                .flatMap(valid -> valid ? chain.filter(exchange) : forbidden(exchange));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"API key không hợp lệ\"}"
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override public int getOrder() { return -9; }
}
