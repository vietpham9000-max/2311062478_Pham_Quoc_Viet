package com.phamquocviet.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<Map<String, Object>> unauthorized(BadCredentialsException ex, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Username và password không được để trống", request.getRequestURI());
    }
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, Object>> notFound(NoSuchElementException ex, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }
    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", LocalDateTime.now()); body.put("status", status.value());
        body.put("error", status.getReasonPhrase()); body.put("message", message); body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}
