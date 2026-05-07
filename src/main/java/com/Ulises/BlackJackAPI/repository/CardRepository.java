package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CardRepository extends ReactiveCrudRepository<CardEntity, Long> {

    @Query("SELECT * FROM cards WHERE hand_id = :handId ORDER BY id")
    Flux<CardEntity> findByHandIdOrderById(Long handId);

    @Query("SELECT * FROM cards WHERE hand_id = :handId")
    Flux<CardEntity> findByHandId(Long handId);
}