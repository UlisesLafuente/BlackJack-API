package com.Ulises.BlackJackAPI.controller;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import com.Ulises.BlackJackAPI.dto.AuthResponse;
import com.Ulises.BlackJackAPI.dto.LoginRequest;
import com.Ulises.BlackJackAPI.dto.RegisterRequest;
import com.Ulises.BlackJackAPI.exception.InvalidCredentialsException;
import com.Ulises.BlackJackAPI.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Test
    void testRegisterRequestValidation() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testRegisterRequestWithNullValues() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        assertNotNull(request.getUsername());
        assertNotNull(request.getPassword());
    }

    @Test
    void testLoginRequestValidation() {
        LoginRequest request = new LoginRequest("testuser", "password");
        assertEquals("testuser", request.getUsername());
        assertEquals("password", request.getPassword());
    }

    @Test
    void testLoginRequestSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("newuser");
        request.setPassword("newpass");
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass", request.getPassword());
    }
}