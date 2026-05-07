package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.model.entity.HandEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HandRepository extends ReactiveCrudRepository<HandEntity, Long> {
    Flux<HandEntity> findByGameId(Long gameId);
    Mono<HandEntity> findByGameIdAndTypeAndHandIndex(Long gameId, com.Ulises.BlackJackAPI.model.enums.HandType type, Integer handIndex);
}