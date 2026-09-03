package com.phamquocviet.authservice.controller;

import com.phamquocviet.authservice.dto.ApiKeyCreateRequestDTO;
import com.phamquocviet.authservice.dto.ApiKeyResponseDTO;
import com.phamquocviet.authservice.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {
    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) { this.service = service; }

    @GetMapping
    public List<ApiKeyResponseDTO> getAll() { return service.getAll(); }

    @PostMapping
    public ResponseEntity<ApiKeyResponseDTO> create(@Valid @RequestBody ApiKeyCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @DeleteMapping("/{id}")
    public ApiKeyResponseDTO revoke(@PathVariable Long id) { return service.revoke(id); }
}
