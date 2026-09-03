package com.phamquocviet.apigateway.filter;

import com.phamquocviet.apigateway.client.AuthServiceClient;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyValidationCacheTests {
    @Test
    void cachesByApiKeyAndScope() {
        AuthServiceClient client = mock(AuthServiceClient.class);
        when(client.validate("key", "COURSE_READ")).thenReturn(Mono.just(true));
        when(client.validate("key", "COURSE_WRITE")).thenReturn(Mono.just(false));
        ApiKeyValidationCache cache = new ApiKeyValidationCache(client);

        assertTrue(cache.validate("key", "COURSE_READ").block());
        assertTrue(cache.validate("key", "COURSE_READ").block());
        assertFalse(cache.validate("key", "COURSE_WRITE").block());

        verify(client, times(1)).validate("key", "COURSE_READ");
        verify(client, times(1)).validate("key", "COURSE_WRITE");
    }

    @Test
    void failsClosedWhenAuthServiceErrors() {
        AuthServiceClient client = mock(AuthServiceClient.class);
        when(client.validate("key", "COURSE_READ")).thenReturn(Mono.error(new IllegalStateException("down")));

        assertFalse(new ApiKeyValidationCache(client).validate("key", "COURSE_READ").block());
    }
}
