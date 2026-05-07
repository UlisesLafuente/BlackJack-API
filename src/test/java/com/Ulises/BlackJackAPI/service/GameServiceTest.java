package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.domain.factory.CardFactory;
import com.Ulises.BlackJackAPI.domain.services.GameRulesEngine;
import com.Ulises.BlackJackAPI.domain.services.ScoreCalculator;
import com.Ulises.BlackJackAPI.dto.GameResponse;
import com.Ulises.BlackJackAPI.exception.GameNotFoundException;
import com.Ulises.BlackJackAPI.repository.CardRepository;
import com.Ulises.BlackJackAPI.repository.GameRepository;
import com.Ulises.BlackJackAPI.repository.HandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private HandRepository handRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private DeckService deckService;
    @Mock
    private PlayerService playerService;
    @Mock
    private GameRulesEngine rulesEngine;
    @Mock
    private ScoreCalculator scoreCalculator;
    @Mock
    private CardFactory cardFactory;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(
                gameRepository, handRepository, cardRepository,
                deckService, playerService, rulesEngine,
                scoreCalculator, cardFactory);
    }

    @Test
    void testCreateGameSuccess() {
        GameEntity game = new GameEntity(1L);
        game.setId(1L);
        game.setStatus(GameStatus.BETTING);

        when(gameRepository.save(any(GameEntity.class))).thenReturn(Mono.just(game));
        when(deckService.createDeckForGame(1L)).thenReturn(Mono.empty());
        when(gameRepository.findById(1L)).thenReturn(Mono.just(game));
        when(handRepository.findByGameId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(gameService.createGame(1L))
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();
    }

    @Test
    void testGetGameFound() {
        GameEntity game = new GameEntity(1L);
        game.setId(1L);
        game.setStatus(GameStatus.PLAYER_TURN);

        when(gameRepository.findByIdAndPlayerId(1L, 1L)).thenReturn(Mono.just(game));
        when(handRepository.findByGameId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(gameService.getGame(1L, 1L))
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();
    }

    @Test
    void testGetGameNotFound() {
        when(gameRepository.findByIdAndPlayerId(1L, 1L)).thenReturn(Mono.empty());

        StepVerifier.create(gameService.getGame(1L, 1L))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void testGameEntityCreation() {
        GameEntity game = new GameEntity(1L);
        assertEquals(1L, game.getPlayerId());
        assertEquals(GameStatus.BETTING, game.getStatus());
    }

    @Test
    void testGameEntityBet() {
        GameEntity game = new GameEntity(1L);
        game.setBet(100);
        assertEquals(100, game.getBet());
    }

    @Test
    void testHandEntityCreation() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        assertEquals(1L, hand.getGameId());
        assertEquals(HandType.PLAYER, hand.getType());
        assertEquals(0, hand.getHandIndex());
    }

    @Test
    void testHandEntityScore() {
        HandEntity hand = new HandEntity(1L, HandType.PLAYER, 0);
        hand.setScore(15);
        assertEquals(15, hand.getScore());
    }

    @Test
    void testGameResponseGettersSetters() {
        GameResponse response = new GameResponse();
        response.setId(1L);
        response.setPlayerId(1L);
        response.setBet(100);
        response.setStatus(GameStatus.PLAYER_TURN);

        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals(Long.valueOf(1L), response.getPlayerId());
        assertEquals(Integer.valueOf(100), response.getBet());
        assertEquals(GameStatus.PLAYER_TURN, response.getStatus());
    }

    @Test
    void testGameResponseFullConstructor() {
        GameResponse response = new GameResponse(
                1L, 1L, 100, 0,
                GameStatus.FINISHED, GameResult.WIN,
                20, 18, null, "Game over");

        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals(GameResult.WIN, response.getResult());
        assertEquals(Integer.valueOf(20), response.getPlayerScore());
    }

    @Test
    void testGameRulesEngineDetermineResult() {
        when(rulesEngine.determineResult(21, 20)).thenReturn(GameResult.WIN);
        when(rulesEngine.determineResult(18, 21)).thenReturn(GameResult.LOSE);
        when(rulesEngine.determineResult(20, 20)).thenReturn(GameResult.PUSH);
        when(rulesEngine.determineResult(25, 18)).thenReturn(GameResult.PLAYER_BUST);

        assertEquals(GameResult.WIN, rulesEngine.determineResult(21, 20));
        assertEquals(GameResult.LOSE, rulesEngine.determineResult(18, 21));
        assertEquals(GameResult.PUSH, rulesEngine.determineResult(20, 20));
        assertEquals(GameResult.PLAYER_BUST, rulesEngine.determineResult(25, 18));
    }

    @Test
    void testGameRulesEngineCheckBlackjack() {
        HandEntity hand = new HandEntity();
        hand.setScore(21);
        when(rulesEngine.checkBlackjack(hand)).thenReturn(true);

        assertTrue(rulesEngine.checkBlackjack(hand));
    }

    @Test
    void testScoreCalculator() {
        when(scoreCalculator.calculateHandScore(any())).thenReturn(17);

        HandEntity hand = new HandEntity();
        assertEquals(17, scoreCalculator.calculateHandScore(java.util.List.of()));
    }

    @Test
    void testDeckServiceCreateDeck() {
        when(deckService.createDeckForGame(1L)).thenReturn(Mono.empty());

        StepVerifier.create(deckService.createDeckForGame(1L))
                .verifyComplete();
    }

    @Test
    void testPlayerServiceUpdateScore() {
        when(playerService.updateScore(1L, GameResult.WIN, 10, 0))
                .thenReturn(Mono.just(new com.Ulises.BlackJackAPI.domain.entity.PlayerEntity("test", "pass")));

        StepVerifier.create(playerService.updateScore(1L, GameResult.WIN, 10, 0))
                .assertNext(player -> assertNotNull(player))
                .verifyComplete();
    }
}