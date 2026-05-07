package com.Ulises.BlackJackAPI.repository;

import com.Ulises.BlackJackAPI.model.entity.CardEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CardRepository extends ReactiveCrudRepository<CardEntity, Long> {
    Flux<CardEntity> findByHandId(Long handId);
    Flux<CardEntity> findByHandIdOrderById(Long handId);
}