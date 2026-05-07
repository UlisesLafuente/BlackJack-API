package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.DeckEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
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

    public Mono<DrawnCard> drawCard(Long gameId) {
        return deckRepository.findFirstAvailableByGameId(gameId)
                .flatMap(card -> {
                    String rankStr = card.getRank();
                    if (rankStr == null) {
                        return Mono.error(new RuntimeException("Card rank is null from database"));
                    }
                    DrawnCard drawnCard = new DrawnCard(card.getSuit(), Rank.valueOf(rankStr));
                    return deckRepository.markAsDrawn(card.getId()).thenReturn(drawnCard);
                });
    }

    public Flux<DrawnCard> drawCards(Long gameId, int count) {
        return deckRepository.findAvailableByGameId(gameId, count)
                .flatMap(card -> {
                    String rankStr = card.getRank();
                    if (rankStr == null) {
                        return Mono.error(new RuntimeException("Card rank is null from database"));
                    }
                    DrawnCard drawnCard = new DrawnCard(card.getSuit(), Rank.valueOf(rankStr));
                    return deckRepository.markAsDrawn(card.getId()).thenReturn(drawnCard);
                });
    }

    public Mono<Void> deleteDeckByGameId(Long gameId) {
        return deckRepository.deleteByGameId(gameId).then();
    }

    public record DrawnCard(Suit suit, Rank rank) {}
}