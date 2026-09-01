package com.phamquocviet.authservice.dto;

public record LoginResponse(Long userId, String token, String username, String role) {}
