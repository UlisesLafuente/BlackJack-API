package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.DeckEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeckRepository extends ReactiveCrudRepository<DeckEntity, Long> {

    @Query("SELECT * FROM decks WHERE game_id = :gameId AND drawn = false ORDER BY id LIMIT 1")
    Mono<DeckEntity> findFirstAvailableByGameId(Long gameId);

    @Query("SELECT * FROM decks WHERE game_id = :gameId AND drawn = false ORDER BY id LIMIT :count")
    Flux<DeckEntity> findAvailableByGameId(Long gameId, int count);

    @Query("UPDATE decks SET drawn = true WHERE id = :id")
    Mono<Void> markAsDrawn(Long id);

    @Query("DELETE FROM decks WHERE game_id = :gameId")
    Mono<Void> deleteByGameId(Long gameId);
}