package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.dto.AuthResponse;
import com.Ulises.BlackJackAPI.dto.LoginRequest;
import com.Ulises.BlackJackAPI.dto.RegisterRequest;
import com.Ulises.BlackJackAPI.exception.PlayerNotFoundException;
import com.Ulises.BlackJackAPI.model.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import com.Ulises.BlackJackAPI.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<AuthResponse> register(RegisterRequest request) {
        return playerRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Username already exists"));
                    }
                    PlayerEntity player = new PlayerEntity(
                            request.getUsername(),
                            passwordEncoder.encode(request.getPassword())
                    );
                    return playerRepository.save(player);
                })
                .flatMap(player -> {
                    String token = jwtUtil.generateToken(player.getUsername(), player.getId());
                    return Mono.just(new AuthResponse(token, player.getId(), player.getUsername(), player.getScore()));
                });
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        return playerRepository.findByUsername(request.getUsername())
                .flatMap(player -> {
                    if (passwordEncoder.matches(request.getPassword(), player.getPassword())) {
                        String token = jwtUtil.generateToken(player.getUsername(), player.getId());
                        return Mono.just(new AuthResponse(token, player.getId(), player.getUsername(), player.getScore()));
                    }
                    return Mono.error(new RuntimeException("Invalid credentials"));
                });
    }

    public Mono<PlayerEntity> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + username)));
    }
}