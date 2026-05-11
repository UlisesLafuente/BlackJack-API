package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.dto.PlayerResponse;
import com.Ulises.BlackJackAPI.dto.RankingResponse;
import com.Ulises.BlackJackAPI.exception.PlayerNotFoundException;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import com.Ulises.BlackJackAPI.domain.services.GameRulesEngine;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private GameRulesEngine rulesEngine;

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(playerRepository, rulesEngine);
    }

    @Test
    void testGetPlayerByIdSuccess() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));

        StepVerifier.create(playerService.getPlayerById(1L))
                .assertNext(response -> {
                    assertEquals(1L, response.getId());
                    assertEquals("testuser", response.getUsername());
                    assertEquals(Integer.valueOf(100), response.getScore());
                })
                .verifyComplete();
    }

    @Test
    void testGetPlayerByIdNotFound() {
        when(playerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.getPlayerById(999L))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }

    @Test
    void testGetPlayerEntityByIdSuccess() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));

        StepVerifier.create(playerService.getPlayerEntityById(1L))
                .assertNext(p -> assertEquals(1L, p.getId()))
                .verifyComplete();
    }

    @Test
    void testGetPlayerEntityByIdNotFound() {
        when(playerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.getPlayerEntityById(999L))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateUsernameSuccess() {
        PlayerEntity player = new PlayerEntity("oldusername", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateUsername(1L, "newusername"))
                .assertNext(response -> {
                    assertEquals("newusername", response.getUsername());
                    assertEquals(Integer.valueOf(100), response.getScore());
                })
                .verifyComplete();
    }

    @Test
    void testUpdateUsernameNotFound() {
        when(playerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.updateUsername(999L, "newusername"))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateScoreWin() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(rulesEngine.calculateScoreChange(GameResult.WIN, 10, 0)).thenReturn(15);
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateScore(1L, GameResult.WIN, 10, 0))
                .assertNext(p -> assertEquals(Integer.valueOf(115), p.getScore()))
                .verifyComplete();
    }

    @Test
    void testUpdateScoreLose() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(rulesEngine.calculateScoreChange(GameResult.LOSE, 10, 0)).thenReturn(-10);
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateScore(1L, GameResult.LOSE, 10, 0))
                .assertNext(p -> assertEquals(Integer.valueOf(90), p.getScore()))
                .verifyComplete();
    }

    @Test
    void testUpdateScoreBlackjack() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(rulesEngine.calculateScoreChange(GameResult.BLACKJACK, 10, 0)).thenReturn(15);
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateScore(1L, GameResult.BLACKJACK, 10, 0))
                .assertNext(p -> assertEquals(Integer.valueOf(115), p.getScore()))
                .verifyComplete();
    }

    @Test
    void testUpdateScorePush() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(rulesEngine.calculateScoreChange(GameResult.PUSH, 10, 0)).thenReturn(0);
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateScore(1L, GameResult.PUSH, 10, 0))
                .assertNext(p -> assertEquals(Integer.valueOf(100), p.getScore()))
                .verifyComplete();
    }

    @Test
    void testUpdateScoreDirectValue() {
        PlayerEntity player = new PlayerEntity("testuser", "password");
        player.setId(1L);
        player.setScore(100);

        when(playerRepository.findById(1L)).thenReturn(Mono.just(player));
        when(playerRepository.save(any(PlayerEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(playerService.updateScore(1L, 50))
                .assertNext(response -> assertEquals(150, response.getScore()))
                .verifyComplete();
    }

    @Test
    void testGetRanking() {
        PlayerEntity player1 = new PlayerEntity("user1", "password");
        player1.setId(1L);
        player1.setScore(200);

        PlayerEntity player2 = new PlayerEntity("user2", "password");
        player2.setId(2L);
        player2.setScore(100);

        when(playerRepository.findAllByOrderByScoreDesc()).thenReturn(Flux.just(player1, player2));

        StepVerifier.create(playerService.getRanking())
                .assertNext(response -> {
                    assertEquals(2, response.getRankings().size());
                    assertEquals("user1", response.getRankings().get(0).getUsername());
                    assertEquals(Integer.valueOf(200), response.getRankings().get(0).getScore());
                })
                .verifyComplete();
    }

    @Test
    void testGetRankingEmpty() {
        when(playerRepository.findAllByOrderByScoreDesc()).thenReturn(Flux.empty());

        StepVerifier.create(playerService.getRanking())
                .assertNext(response -> assertTrue(response.getRankings().isEmpty()))
                .verifyComplete();
    }
}