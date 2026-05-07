package com.Ulises.BlackJackAPI.controller;

import com.Ulises.BlackJackAPI.dto.*;
import com.Ulises.BlackJackAPI.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for authentication operations.
 * Handles player registration and login.
 *
 * @author Ulises Lafuente
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<AuthResponse>>> register(@RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Player registered successfully", response)));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<AuthResponse>>> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Login successful", response)));
    }
}