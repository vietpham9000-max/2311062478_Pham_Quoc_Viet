package com.phamquocviet.authservice.service;

import com.phamquocviet.authservice.dto.ApiKeyCreateRequestDTO;
import com.phamquocviet.authservice.dto.ApiKeyResponseDTO;
import com.phamquocviet.authservice.entity.ApiKey;
import com.phamquocviet.authservice.repository.ApiKeyRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiKeyServiceTests {
    private final ApiKeyRepository repository = mock(ApiKeyRepository.class);
    private final ApiKeyService service = new ApiKeyService(repository);

    @Test
    void createsPrefixedKeyAndNormalizesScopes() {
        when(repository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ApiKeyCreateRequestDTO request = new ApiKeyCreateRequestDTO();
        request.setOwnerName(" Partner ");
        request.setScopes(" COURSE_READ, COURSE_WRITE ");
        request.setValidDays(30);

        ApiKeyResponseDTO result = service.create(request);

        assertTrue(result.keyValue().startsWith("crs_"));
        assertEquals(36, result.keyValue().length());
        assertEquals("Partner", result.ownerName());
        assertEquals("COURSE_READ,COURSE_WRITE", result.scopes());
        assertEquals("ACTIVE", result.status());
        assertNotNull(result.expiresAt());
    }

    @Test
    void validatesOnlyExactScopeOnActiveUnexpiredKey() {
        ApiKey key = key("ACTIVE", "COURSE_READ,COURSE_WRITE", LocalDateTime.now().plusMinutes(1));
        when(repository.findByKeyValue("key")).thenReturn(Optional.of(key));

        assertTrue(service.isValidForScope("key", "COURSE_READ"));
        assertFalse(service.isValidForScope("key", "COURSE"));
    }

    @Test
    void rejectsRevokedKey() {
        when(repository.findByKeyValue("key"))
                .thenReturn(Optional.of(key("REVOKED", "COURSE_READ", null)));
        assertFalse(service.isValidForScope("key", "COURSE_READ"));
    }

    @Test
    void rejectsExpiredKey() {
        when(repository.findByKeyValue("key"))
                .thenReturn(Optional.of(key("ACTIVE", "COURSE_READ", LocalDateTime.now().minusSeconds(1))));
        assertFalse(service.isValidForScope("key", "COURSE_READ"));
    }

    @Test
    void revocationUpdatesStatusWithoutDeleting() {
        ApiKey key = key("ACTIVE", "COURSE_READ", null);
        when(repository.findById(1L)).thenReturn(Optional.of(key));
        when(repository.save(key)).thenReturn(key);

        assertEquals("REVOKED", service.revoke(1L).status());
        verify(repository).save(key);
        verify(repository, never()).delete(any());
    }

    private ApiKey key(String status, String scopes, LocalDateTime expiresAt) {
        ApiKey key = new ApiKey();
        key.setKeyValue("key");
        key.setOwnerName("Partner");
        key.setScopes(scopes);
        key.setStatus(status);
        key.setCreatedAt(LocalDateTime.now());
        key.setExpiresAt(expiresAt);
        return key;
    }
}
