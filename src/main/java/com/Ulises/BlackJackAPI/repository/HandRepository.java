package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HandRepository extends ReactiveCrudRepository<HandEntity, Long> {

    @Query("SELECT * FROM hands WHERE game_id = :gameId")
    Flux<HandEntity> findByGameId(Long gameId);

    @Query("SELECT * FROM hands WHERE game_id = :gameId AND type = :type AND hand_index = :handIndex")
    Mono<HandEntity> findByGameIdAndTypeAndHandIndex(Long gameId, HandType type, Integer handIndex);
}