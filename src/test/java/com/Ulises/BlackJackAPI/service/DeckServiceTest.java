package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.DeckEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckService = new DeckService(deckRepository);
    }

    @Test
    void testCreateDeckForGame() {
        doReturn(Flux.empty()).when(deckRepository).saveAll(any(Iterable.class));

        StepVerifier.create(deckService.createDeckForGame(1L))
                .verifyComplete();
    }

    @Test
    void testDrawCard() {
        DeckEntity deckEntity = new DeckEntity(1L, Suit.HEARTS, Rank.ACE);
        deckEntity.setId(1L);
        deckEntity.setDrawn(false);

        when(deckRepository.findFirstAvailableByGameId(1L)).thenReturn(Mono.just(deckEntity));
        when(deckRepository.markAsDrawn(1L)).thenReturn(Mono.empty());

        StepVerifier.create(deckService.drawCard(1L))
                .assertNext(card -> {
                    assertEquals(Suit.HEARTS, card.suit());
                    assertEquals(Rank.ACE, card.rank());
                })
                .verifyComplete();
    }

    @Test
    void testDrawCardNoCardsAvailable() {
        when(deckRepository.findFirstAvailableByGameId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(deckService.drawCard(1L))
                .expectError();
    }

    @Test
    void testDrawCardsMultiple() {
        DeckEntity card1 = new DeckEntity(1L, Suit.HEARTS, Rank.ACE);
        card1.setId(1L);
        card1.setDrawn(false);

        DeckEntity card2 = new DeckEntity(1L, Suit.SPADES, Rank.KING);
        card2.setId(2L);
        card2.setDrawn(false);

        when(deckRepository.findAvailableByGameId(1L, 2)).thenReturn(Flux.just(card1, card2));
        when(deckRepository.markAsDrawn(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(deckService.drawCards(1L, 2))
                .assertNext(card -> {
                    assertEquals(Suit.HEARTS, card.suit());
                    assertEquals(Rank.ACE, card.rank());
                })
                .assertNext(card -> {
                    assertEquals(Suit.SPADES, card.suit());
                    assertEquals(Rank.KING, card.rank());
                })
                .verifyComplete();
    }

    @Test
    void testDeleteDeckByGameId() {
        when(deckRepository.deleteByGameId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(deckService.deleteDeckByGameId(1L))
                .verifyComplete();
    }

    @Test
    void testDrawnCardRecord() {
        DeckService.DrawnCard card = new DeckService.DrawnCard(Suit.HEARTS, Rank.KING);
        assertEquals(Suit.HEARTS, card.suit());
        assertEquals(Rank.KING, card.rank());
    }
}