package com.matchiq.matchiq.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.matchiq.matchiq.Dto.SignupRequest;
import com.matchiq.matchiq.Dto.LoginRequest;
import com.matchiq.matchiq.entity.User;
import com.matchiq.matchiq.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(
            @Valid @RequestBody SignupRequest request) {

        User user = authService.register(request);

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        Map<String, Object> response = Map.of(
                "accessToken", token,
                "tokenType", "Bearer",
                "expiresIn", 3600
        );

        return ResponseEntity.ok(response);
    }
}