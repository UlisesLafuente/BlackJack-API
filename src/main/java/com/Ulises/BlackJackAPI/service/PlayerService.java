package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.dto.PlayerResponse;
import com.Ulises.BlackJackAPI.dto.RankingResponse;
import com.Ulises.BlackJackAPI.exception.PlayerNotFoundException;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import com.Ulises.BlackJackAPI.domain.services.GameRulesEngine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for managing player operations.
 * Handles player retrieval, username updates, score management, and rankings.
 *
 * @author Ulises Lafuente
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameRulesEngine rulesEngine;

    public PlayerService(PlayerRepository playerRepository, GameRulesEngine rulesEngine) {
        this.playerRepository = playerRepository;
        this.rulesEngine = rulesEngine;
    }

    public Mono<PlayerResponse> getPlayerById(Long id) {
        return playerRepository.findById(id)
                .map(p -> new PlayerResponse(p.getId(), p.getUsername(), p.getScore()))
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)));
    }

    public Mono<PlayerEntity> getPlayerEntityById(Long id) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)));
    }

    public Mono<PlayerResponse> updateUsername(Long playerId, String newUsername) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setUsername(newUsername);
                    return playerRepository.save(player);
                })
                .map(p -> new PlayerResponse(p.getId(), p.getUsername(), p.getScore()))
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + playerId)));
    }

    public Mono<PlayerEntity> updateScore(Long playerId, GameResult result, int bet, int insuranceBet) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setScore(player.getScore() + rulesEngine.calculateScoreChange(result, bet, insuranceBet));
                    return playerRepository.save(player);
                });
    }

    public Mono<PlayerResponse> updateScore(Long playerId, int scoreChange) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setScore(player.getScore() + scoreChange);
                    return playerRepository.save(player);
                })
                .map(p -> new PlayerResponse(p.getId(), p.getUsername(), p.getScore()))
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + playerId)));
    }

    public Mono<RankingResponse> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc()
                .map(p -> new PlayerResponse(p.getId(), p.getUsername(), p.getScore()))
                .collectList()
                .map(RankingResponse::new);
    }
}