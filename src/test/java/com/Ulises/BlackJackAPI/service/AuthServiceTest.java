package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.dto.AuthResponse;
import com.Ulises.BlackJackAPI.dto.LoginRequest;
import com.Ulises.BlackJackAPI.dto.RegisterRequest;
import com.Ulises.BlackJackAPI.exception.PlayerNotFoundException;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import com.Ulises.BlackJackAPI.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(playerRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");

        when(playerRepository.existsByUsername("testuser")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> {
            PlayerEntity player = invocation.getArgument(0);
            player.setId(1L);
            return Mono.just(player);
        });
        when(jwtUtil.generateToken(anyString(), any())).thenReturn("testToken");

        StepVerifier.create(authService.register(request))
                .assertNext(response -> {
                    assertEquals("testToken", response.getToken());
                    assertEquals("testuser", response.getUsername());
                    assertEquals(1L, response.getPlayerId());
                })
                .verifyComplete();
    }

    @Test
    void testRegisterUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("existinguser", "password123");

        when(playerRepository.existsByUsername("existinguser")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        PlayerEntity player = new PlayerEntity("testuser", "encodedPassword");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findByUsername("testuser")).thenReturn(Mono.just(player));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any())).thenReturn("testToken");

        StepVerifier.create(authService.login(request))
                .assertNext(response -> {
                    assertEquals("testToken", response.getToken());
                    assertEquals("testuser", response.getUsername());
                })
                .verifyComplete();
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        PlayerEntity player = new PlayerEntity("testuser", "encodedPassword");

        when(playerRepository.findByUsername("testuser")).thenReturn(Mono.just(player));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        StepVerifier.create(authService.login(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testLoginPlayerNotFound() {
        LoginRequest request = new LoginRequest("nonexistent", "password");

        when(playerRepository.findByUsername("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(request))
                .verifyComplete();
    }

    @Test
    void testGetPlayerByUsernameSuccess() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);

        when(playerRepository.findByUsername("testuser")).thenReturn(Mono.just(player));

        StepVerifier.create(authService.getPlayerByUsername("testuser"))
                .assertNext(p -> {
                    assertEquals("testuser", p.getUsername());
                    assertEquals(1L, p.getId());
                })
                .verifyComplete();
    }

    @Test
    void testGetPlayerByUsernameNotFound() {
        when(playerRepository.findByUsername("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(authService.getPlayerByUsername("nonexistent"))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }
}