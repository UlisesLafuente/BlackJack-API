package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.domain.factory.CardFactory;
import com.Ulises.BlackJackAPI.domain.services.ScoreCalculator;
import com.Ulises.BlackJackAPI.dto.CardResponse;
import com.Ulises.BlackJackAPI.dto.HandResponse;
import com.Ulises.BlackJackAPI.exception.InvalidMoveException;
import com.Ulises.BlackJackAPI.repository.CardRepository;
import com.Ulises.BlackJackAPI.repository.HandRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandServiceTest {

    @Mock
    private HandRepository handRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private DeckService deckService;
    @Mock
    private CardFactory cardFactory;
    @Mock
    private ScoreCalculator scoreCalculator;

    private HandService handService;

    @BeforeEach
    void setUp() {
        handService = new HandService(handRepository, cardRepository, deckService, cardFactory, scoreCalculator);
    }

    @Test
    void testCreatePlayerAndCroupierHands() {
        GameEntity game = new GameEntity(1L);
        game.setId(1L);

        HandEntity playerHand = new HandEntity(1L, HandType.PLAYER, 0);
        playerHand.setId(1L);
        HandEntity croupierHand = new HandEntity(1L, HandType.CROUPIER, 0);
        croupierHand.setId(2L);

        when(handRepository.save(any(HandEntity.class))).thenReturn(
                Mono.just(playerHand),
                Mono.just(croupierHand)
        );

        StepVerifier.create(handService.createPlayerAndCroupierHands(game))
                .assertNext(hand -> {
                    assertEquals(HandType.PLAYER, hand.getType());
                    assertEquals(0, hand.getHandIndex());
                })
                .verifyComplete();
    }

    @Test
    void testGetPlayerHandFound() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        hand.setId(1L);
        hand.setScore(15);

        when(handRepository.findByGameIdAndTypeAndHandIndex(1L, HandType.PLAYER, 0))
                .thenReturn(Mono.just(hand));

        StepVerifier.create(handService.getPlayerHand(1L, 0))
                .assertNext(h -> {
                    assertEquals(1L, h.getId());
                    assertEquals(15, h.getScore());
                })
                .verifyComplete();
    }

    @Test
    void testGetPlayerHandNotFound() {
        when(handRepository.findByGameIdAndTypeAndHandIndex(1L, HandType.PLAYER, 0))
                .thenReturn(Mono.empty());

        StepVerifier.create(handService.getPlayerHand(1L, 0))
                .expectError(InvalidMoveException.class)
                .verify();
    }

    @Test
    void testGetCroupierHand() {
        HandEntity hand = new HandEntity(1L, HandType.CROUPIER, 0);
        hand.setId(1L);

        when(handRepository.findByGameIdAndTypeAndHandIndex(1L, HandType.CROUPIER, 0))
                .thenReturn(Mono.just(hand));

        StepVerifier.create(handService.getCroupierHand(1L))
                .assertNext(h -> assertEquals(HandType.CROUPIER, h.getType()))
                .verifyComplete();
    }

    @Test
    void testUpdateHandScore() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        hand.setId(1L);
        hand.setScore(0);

        CardEntity card = new CardEntity(1L, Suit.HEARTS, Rank.ACE, false);

        when(cardRepository.findByHandIdOrderById(1L)).thenReturn(Flux.just(card));
        when(scoreCalculator.calculateHandScore(List.of(card))).thenReturn(11);
        when(handRepository.save(any(HandEntity.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(handService.updateHandScore(hand))
                .assertNext(h -> assertEquals(11, h.getScore()))
                .verifyComplete();
    }

    @Test
    void testIsPlayerHandBust() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        hand.setScore(25);

        when(handRepository.findByGameIdAndTypeAndHandIndex(1L, HandType.PLAYER, 0))
                .thenReturn(Mono.just(hand));

        StepVerifier.create(handService.isPlayerBust(1L, 0))
                .assertNext(bust -> assertTrue(bust))
                .verifyComplete();
    }

    @Test
    void testIsPlayerHandNotBust() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        hand.setScore(15);

        when(handRepository.findByGameIdAndTypeAndHandIndex(1L, HandType.PLAYER, 0))
                .thenReturn(Mono.just(hand));

        StepVerifier.create(handService.isPlayerBust(1L, 0))
                .assertNext(bust -> assertFalse(bust))
                .verifyComplete();
    }
}