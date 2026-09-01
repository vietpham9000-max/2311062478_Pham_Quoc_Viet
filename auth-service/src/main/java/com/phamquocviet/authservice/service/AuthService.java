package com.phamquocviet.authservice.service;

import com.phamquocviet.authservice.dto.*;
import com.phamquocviet.authservice.entity.User;
import com.phamquocviet.authservice.repository.UserRepository;
import com.phamquocviet.authservice.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.users = users; this.passwordEncoder = passwordEncoder; this.jwtUtil = jwtUtil;
    }
    public LoginResponse login(LoginRequest request) {
        User user = users.findByUsername(request.getUsername())
                .filter(found -> passwordEncoder.matches(request.getPassword(), found.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Sai username hoặc password"));
        return new LoginResponse(
                user.getId(),
                jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole()),
                user.getUsername(),
                user.getRole()
        );
    }
}
