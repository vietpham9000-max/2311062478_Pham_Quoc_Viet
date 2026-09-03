package com.phamquocviet.authservice.service;

import com.phamquocviet.authservice.dto.ApiKeyCreateRequestDTO;
import com.phamquocviet.authservice.dto.ApiKeyResponseDTO;
import com.phamquocviet.authservice.entity.ApiKey;
import com.phamquocviet.authservice.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ApiKeyService {
    static final String ACTIVE = "ACTIVE";
    static final String REVOKED = "REVOKED";
    private static final int RANDOM_BYTES = 24;

    private final ApiKeyRepository repository;
    private final SecureRandom secureRandom;

    @Autowired
    public ApiKeyService(ApiKeyRepository repository) {
        this(repository, new SecureRandom());
    }

    ApiKeyService(ApiKeyRepository repository, SecureRandom secureRandom) {
        this.repository = repository;
        this.secureRandom = secureRandom;
    }

    @Transactional
    public ApiKeyResponseDTO create(ApiKeyCreateRequestDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(generateUniqueKey());
        apiKey.setOwnerName(dto.getOwnerName().trim());
        apiKey.setScopes(normalizeScopes(dto.getScopes()));
        apiKey.setStatus(ACTIVE);
        apiKey.setCreatedAt(now);
        apiKey.setExpiresAt(dto.getValidDays() == null ? null : now.plusDays(dto.getValidDays()));
        return toResponse(repository.save(apiKey));
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponseDTO> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ApiKeyResponseDTO revoke(Long id) {
        ApiKey apiKey = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy API key với id: " + id));
        apiKey.setStatus(REVOKED);
        return toResponse(repository.save(apiKey));
    }

    @Transactional(readOnly = true)
    public boolean isValidForScope(String keyValue, String requiredScope) {
        if (keyValue == null || keyValue.isBlank() || requiredScope == null || requiredScope.isBlank()) return false;
        LocalDateTime now = LocalDateTime.now();
        return repository.findByKeyValue(keyValue)
                .filter(apiKey -> ACTIVE.equals(apiKey.getStatus()))
                .filter(apiKey -> apiKey.getExpiresAt() == null || apiKey.getExpiresAt().isAfter(now))
                .map(ApiKey::getScopes)
                .stream()
                .flatMap(scopes -> List.of(scopes.split(",")).stream())
                .map(String::trim)
                .anyMatch(requiredScope.trim()::equals);
    }

    private String generateUniqueKey() {
        String candidate;
        do {
            byte[] bytes = new byte[RANDOM_BYTES];
            secureRandom.nextBytes(bytes);
            candidate = "crs_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } while (repository.findByKeyValue(candidate).isPresent());
        return candidate;
    }

    private String normalizeScopes(String scopes) {
        return List.of(scopes.split(",")).stream()
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .distinct()
                .reduce((left, right) -> left + "," + right)
                .orElseThrow(() -> new IllegalArgumentException("Scopes không được để trống"));
    }

    private ApiKeyResponseDTO toResponse(ApiKey apiKey) {
        return new ApiKeyResponseDTO(apiKey.getId(), apiKey.getKeyValue(), apiKey.getOwnerName(), apiKey.getScopes(),
                apiKey.getStatus(), apiKey.getExpiresAt(), apiKey.getCreatedAt());
    }
}
