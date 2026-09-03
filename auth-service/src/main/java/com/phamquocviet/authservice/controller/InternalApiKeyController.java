package com.phamquocviet.authservice.controller;

import com.phamquocviet.authservice.service.ApiKeyService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/internal/api-keys")
public class InternalApiKeyController {
    private final ApiKeyService service;

    public InternalApiKeyController(ApiKeyService service) { this.service = service; }

    @GetMapping("/validate")
    public Map<String, Boolean> validate(@RequestParam String key, @RequestParam String scope) {
        return Map.of("valid", service.isValidForScope(key, scope));
    }
}
