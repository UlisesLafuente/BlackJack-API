package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.dto.PlayerResponse;
import com.Ulises.BlackJackAPI.dto.RankingResponse;
import com.Ulises.BlackJackAPI.exception.PlayerNotFoundException;
import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
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

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
                    int scoreChange = calculateScoreChange(result, bet, insuranceBet);
                    player.setScore(player.getScore() + scoreChange);
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

    private int calculateScoreChange(GameResult result, int bet, int insuranceBet) {
        return switch (result) {
            case WIN, BLACKJACK -> (int) (bet * 1.5);
            case LOSE, PLAYER_BUST -> -bet;
            case PUSH -> 0;
            case INSURANCE_WIN -> insuranceBet * 2;
            case INSURANCE_LOSE -> -insuranceBet;
        };
    }
}