package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for Game entities.
 * Provides reactive data access operations for game management.
 *
 * @author Ulises Lafuente
 */
public interface GameRepository extends ReactiveCrudRepository<GameEntity, Long> {

    @Query("SELECT * FROM games WHERE player_id = :playerId")
    Flux<GameEntity> findByPlayerId(Long playerId);

    @Query("SELECT * FROM games WHERE id = :id AND player_id = :playerId")
    Mono<GameEntity> findByIdAndPlayerId(Long id, Long playerId);

    @Query("SELECT * FROM games ORDER BY player_score DESC LIMIT 10")
    Flux<GameEntity> findTop10ByOrderByPlayerScoreDesc();
}