package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Repository for Card entities.
 * Provides reactive data access operations for card management.
 *
 * @author Ulises Lafuente
 */
public interface CardRepository extends ReactiveCrudRepository<CardEntity, Long> {

    @Query("SELECT * FROM cards WHERE hand_id = :handId ORDER BY id")
    Flux<CardEntity> findByHandIdOrderById(Long handId);

    @Query("SELECT * FROM cards WHERE hand_id = :handId")
    Flux<CardEntity> findByHandId(Long handId);

    @Query("SELECT c.* FROM cards c " +
           "INNER JOIN hands h ON c.hand_id = h.id " +
           "WHERE h.game_id = :gameId ORDER BY h.id, c.id")
    Flux<CardEntity> findAllByGameId(Long gameId);
}