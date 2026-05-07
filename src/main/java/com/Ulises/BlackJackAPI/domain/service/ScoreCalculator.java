package com.Ulises.BlackJackAPI.domain.service;

import com.Ulises.BlackJackAPI.domain.game.Card;
import com.Ulises.BlackJackAPI.domain.game.Hand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreCalculator {

    public int calculateHandScore(List<Card> cards) {
        int score = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.isHidden()) continue;
            score += card.getValue();
            if (card.isAce()) aces++;
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public int calculateHandScore(List<Card> cards, boolean hideFirst) {
        if (hideFirst && !cards.isEmpty()) {
            Card firstCard = cards.get(0);
            firstCard.setHidden(true);
        }
        return calculateHandScore(cards);
    }

    public void updateHandScore(Hand hand) {
        hand.setScore(calculateHandScore(hand.getCards()));
    }

    public boolean isBusted(Hand hand) {
        return hand.getScore() > 21;
    }

    public boolean isBlackjack(Hand hand) {
        return hand.getScore() == 21 && hand.getCards().size() == 2;
    }

    public boolean shouldDealerHit(Hand dealerHand) {
        return dealerHand.getScore() < 17;
    }

    public boolean shouldDealerHit(com.Ulises.BlackJackAPI.model.entity.HandEntity dealerHand) {
        return dealerHand.getScore() < 17;
    }
}