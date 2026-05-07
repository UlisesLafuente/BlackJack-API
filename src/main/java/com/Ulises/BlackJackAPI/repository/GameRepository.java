package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.model.entity.GameEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GameRepository extends ReactiveCrudRepository<GameEntity, Long> {
    Flux<GameEntity> findByPlayerId(Long playerId);
    Mono<GameEntity> findByIdAndPlayerId(Long id, Long playerId);
    Flux<GameEntity> findTop10ByOrderByPlayerScoreDesc();
}