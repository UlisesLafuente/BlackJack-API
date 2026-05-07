package com.Ulises.BlackJackAPI.domain.services;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.valueobject.Card;
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

    public int calculateHandScoreFromEntities(List<CardEntity> cards) {
        int score = 0;
        int aces = 0;

        for (CardEntity card : cards) {
            if (Boolean.TRUE.equals(card.getIsHidden())) continue;
            score += card.getValue();
            if (card.getRank() == com.Ulises.BlackJackAPI.domain.enums.Rank.ACE) aces++;
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public boolean shouldDealerHit(HandEntity dealerHand) {
        return dealerHand.getScore() < 17;
    }
}