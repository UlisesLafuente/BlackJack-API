package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.model.entity.DeckEntity;
import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;
import com.Ulises.BlackJackAPI.model.valueobject.Card;
import com.Ulises.BlackJackAPI.repository.DeckRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;

    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Mono<Void> createDeckForGame(Long gameId) {
        List<DeckEntity> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new DeckEntity(gameId, suit, rank));
            }
        }
        Collections.shuffle(deck);
        return deckRepository.saveAll(deck).then();
    }

    public Mono<Card> drawCard(Long gameId) {
        return deckRepository.findFirstByGameIdAndDrawnFalseOrderById(gameId)
                .flatMap(card -> {
                    card.setDrawn(true);
                    return deckRepository.save(card)
                            .map(saved -> new Card(saved.getSuit(), saved.getRank()));
                });
    }

    public Flux<Card> drawCards(Long gameId, int count) {
        return deckRepository.findByGameIdAndDrawnFalseOrderById(gameId)
                .take(count)
                .flatMap(card -> {
                    card.setDrawn(true);
                    return deckRepository.save(card)
                            .map(saved -> new Card(saved.getSuit(), saved.getRank()));
                });
    }

    public Mono<Void> deleteDeckByGameId(Long gameId) {
        return deckRepository.deleteByGameId(gameId).then();
    }
}