package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveCrudRepository<PlayerEntity, Long> {
    Mono<PlayerEntity> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
    Flux<PlayerEntity> findAllByOrderByScoreDesc();
}