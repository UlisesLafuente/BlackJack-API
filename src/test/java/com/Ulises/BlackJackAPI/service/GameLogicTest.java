package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.domain.valueobject.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    @Test
    void testCalculateScoreNoAces() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.SEVEN)
        );
        int score = calculateScore(hand);
        assertEquals(12, score);
    }

    @Test
    void testCalculateScoreWithAce21() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.KING)
        );
        int score = calculateScore(hand);
        assertEquals(21, score);
    }

    @Test
    void testCalculateScoreWithAce1() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.SIX),
            new Card(Suit.DIAMONDS, Rank.FIVE)
        );
        int score = calculateScore(hand);
        assertEquals(12, score);
    }

    @Test
    void testCalculateScoreMultipleAces() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.ACE),
            new Card(Suit.DIAMONDS, Rank.NINE)
        );
        int score = calculateScore(hand);
        assertEquals(21, score);
    }

    @Test
    void testCalculateScoreBust() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.KING),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.SIX)
        );
        int score = calculateScore(hand);
        assertEquals(23, score);
    }

    @Test
    void testBlackjackDetection() {
        List<Card> hand = List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.KING)
        );
        int score = calculateScore(hand);
        assertTrue(score == 21 && hand.size() == 2);
    }

    private int calculateScore(List<Card> cards) {
        int score = 0;
        int aces = 0;

        for (Card card : cards) {
            score += card.getValue();
            if (card.isAce()) {
                aces++;
            }
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }
}