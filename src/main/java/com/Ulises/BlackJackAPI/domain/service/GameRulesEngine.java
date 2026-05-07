package com.Ulises.BlackJackAPI.domain.service;

import com.Ulises.BlackJackAPI.domain.game.Game;
import com.Ulises.BlackJackAPI.domain.game.Hand;
import com.Ulises.BlackJackAPI.model.entity.HandEntity;
import com.Ulises.BlackJackAPI.model.enums.GameResult;
import com.Ulises.BlackJackAPI.model.enums.GameStatus;
import com.Ulises.BlackJackAPI.model.enums.HandType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRulesEngine {

    public void applyBet(Game game, int amount, int playerScore) {
        if (!game.canPlaceBet()) {
            throw new IllegalStateException("Cannot place bet: not in betting phase");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }
        if (playerScore < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        game.placeBet(amount);
    }

    public void applyInsurance(Game game, int playerScore) {
        if (!game.canBuyInsurance()) {
            throw new IllegalStateException("Cannot buy insurance now");
        }
        int insuranceAmount = game.getBet() / 2;
        if (playerScore < insuranceAmount) {
            throw new IllegalStateException("Insufficient funds for insurance");
        }
        game.buyInsurance(insuranceAmount);
    }

    public boolean checkBlackjack(Hand hand) {
        return hand.isBlackjack();
    }

    public boolean checkBlackjack(HandEntity hand) {
        return hand.getScore() == 21;
    }

    public boolean canDealerShowCard(List<Hand> hands) {
        return hands.stream().anyMatch(h -> h.getType() == HandType.PLAYER);
    }

    public boolean allPlayerHandsPlayed(List<HandEntity> hands, HandEntity currentHand) {
        return hands.stream()
                .filter(h -> h.getType() == HandType.PLAYER)
                .allMatch(h -> h.getScore() > 21 || h.getHandIndex() == currentHand.getHandIndex());
    }

    public GameResult determineResult(Hand playerHand, Hand dealerHand) {
        int playerScore = playerHand.getScore();
        int dealerScore = dealerHand.getScore();
        return determineResultByScores(playerScore, dealerScore);
    }

    public GameResult determineResult(int playerScore, int dealerScore) {
        return determineResultByScores(playerScore, dealerScore);
    }

    private GameResult determineResultByScores(int playerScore, int dealerScore) {
        if (playerScore > 21) {
            return GameResult.PLAYER_BUST;
        }
        if (dealerScore > 21) {
            return GameResult.WIN;
        }
        if (playerScore > dealerScore) {
            return GameResult.WIN;
        }
        if (playerScore < dealerScore) {
            return GameResult.LOSE;
        }
        return GameResult.PUSH;
    }

    public int calculateScoreChange(GameResult result, int bet, int insuranceBet) {
        return switch (result) {
            case WIN, BLACKJACK -> (int) (bet * 1.5);
            case LOSE, PLAYER_BUST -> -bet;
            case PUSH -> 0;
            case INSURANCE_WIN -> insuranceBet * 2;
            case INSURANCE_LOSE -> -insuranceBet;
        };
    }

    public boolean isFirstDealerCardAce(List<com.Ulises.BlackJackAPI.domain.game.Card> cards) {
        return !cards.isEmpty() && cards.get(0).isAce();
    }

    public boolean isFirstDealerCardAce(List<com.Ulises.BlackJackAPI.model.entity.CardEntity> cards, boolean hidden) {
        if (cards.isEmpty()) return false;
        var firstCard = cards.get(0);
        return firstCard.getRank() == com.Ulises.BlackJackAPI.model.enums.Rank.ACE;
    }
}