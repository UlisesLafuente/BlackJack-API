package com.Ulises.BlackJackAPI.domain.services;

import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRulesEngine {

    public boolean checkBlackjack(HandEntity hand) {
        return hand.getScore() == 21;
    }

    public boolean canDealerShowCard(List<HandEntity> hands) {
        return hands.stream().anyMatch(h -> h.getType() == HandType.PLAYER);
    }

    public boolean allPlayerHandsPlayed(List<HandEntity> hands, HandEntity currentHand) {
        return hands.stream()
                .filter(h -> h.getType() == HandType.PLAYER)
                .allMatch(h -> h.getScore() > 21 || h.getHandIndex() == currentHand.getHandIndex());
    }

    public GameResult determineResult(int playerScore, int dealerScore) {
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

    public boolean isFirstDealerCardAce(List<com.Ulises.BlackJackAPI.domain.entity.CardEntity> cards) {
        if (cards.isEmpty()) return false;
        var firstCard = cards.get(0);
        return firstCard.getRank() == com.Ulises.BlackJackAPI.domain.enums.Rank.ACE;
    }
}