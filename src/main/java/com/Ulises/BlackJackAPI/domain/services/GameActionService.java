package com.Ulises.BlackJackAPI.domain.services;

import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameActionService {

    private final ScoreCalculator scoreCalculator;
    private final GameRulesEngine rulesEngine;

    public GameActionService(ScoreCalculator scoreCalculator, GameRulesEngine rulesEngine) {
        this.scoreCalculator = scoreCalculator;
        this.rulesEngine = rulesEngine;
    }

    public boolean canHit(GameEntity game) {
        return game.getStatus() == GameStatus.PLAYER_TURN;
    }

    public boolean canStand(GameEntity game) {
        return game.getStatus() == GameStatus.PLAYER_TURN;
    }

    public boolean canDoubleDown(GameEntity game, HandEntity hand) {
        return game.getStatus() == GameStatus.PLAYER_TURN &&
               hand.getType() == HandType.PLAYER &&
               hand.getScore() >= 9 && hand.getScore() <= 11;
    }

    public boolean canSplit(HandEntity hand) {
        return hand.getType() == HandType.PLAYER &&
               hand.getHandIndex() == 0;
    }

    public boolean canBuyInsurance(GameEntity game) {
        return game.getStatus() == GameStatus.PLAYER_TURN && game.getInsuranceBet() == 0;
    }

    public boolean isGameFinished(GameEntity game) {
        return game.getStatus() == GameStatus.FINISHED;
    }

    public boolean allPlayerHandsBusted(List<HandEntity> hands, HandEntity currentHand) {
        return hands.stream()
                .filter(h -> h.getType() == HandType.PLAYER)
                .allMatch(h -> h.getScore() > 21 || h.getHandIndex() == currentHand.getHandIndex());
    }
}