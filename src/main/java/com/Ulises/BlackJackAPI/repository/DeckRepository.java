package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.model.entity.DeckEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeckRepository extends ReactiveCrudRepository<DeckEntity, Long> {
    Flux<DeckEntity> findByGameIdAndDrawnFalseOrderById(Long gameId);
    Mono<DeckEntity> findFirstByGameIdAndDrawnFalseOrderById(Long gameId);
    Flux<DeckEntity> findByGameId(Long gameId);
    Mono<Void> deleteByGameId(Long gameId);
}