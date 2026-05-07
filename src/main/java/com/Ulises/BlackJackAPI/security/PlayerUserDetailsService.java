package com.Ulises.BlackJackAPI.security;

import com.Ulises.BlackJackAPI.model.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlayerUserDetailsService implements ReactiveUserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerUserDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return playerRepository.findByUsername(username)
                .map(PlayerUserDetails::new);
    }

    public Mono<PlayerEntity> findPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }
}