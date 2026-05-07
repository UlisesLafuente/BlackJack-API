package com.Ulises.BlackJackAPI.domain.services;

import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import com.Ulises.BlackJackAPI.repository.PlayerRepository;
import reactor.core.publisher.Mono;

@org.springframework.stereotype.Service
public class PlayerScoreService {

    private final PlayerRepository playerRepository;

    public PlayerScoreService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Mono<PlayerEntity> updateScore(Long playerId, GameResult result, int bet, int insuranceBet) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    int scoreChange = calculateScoreChange(result, bet, insuranceBet);
                    player.setScore(player.getScore() + scoreChange);
                    return playerRepository.save(player);
                });
    }

    public Mono<Boolean> hasSufficientFunds(Long playerId, int amount) {
        return playerRepository.findById(playerId)
                .map(player -> player.getScore() >= amount);
    }

    public Mono<PlayerEntity> getPlayer(Long playerId) {
        return playerRepository.findById(playerId);
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