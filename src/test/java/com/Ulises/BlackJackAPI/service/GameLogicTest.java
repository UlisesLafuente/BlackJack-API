package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.domain.services.ScoreCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    private ScoreCalculator scoreCalculator;

    @BeforeEach
    void setUp() {
        scoreCalculator = new ScoreCalculator();
    }

    private CardEntity createCard(Suit suit, Rank rank) {
        CardEntity card = new CardEntity();
        card.setSuit(suit);
        card.setRank(rank);
        card.setValue(rank.getValue());
        card.setIsHidden(false);
        return card;
    }

    @Test
    void testCalculateScoreNoAces() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.FIVE),
            createCard(Suit.CLUBS, Rank.SEVEN)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(12, score);
    }

    @Test
    void testCalculateScoreWithAce21() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.ACE),
            createCard(Suit.CLUBS, Rank.KING)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(21, score);
    }

    @Test
    void testCalculateScoreWithAce1() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.ACE),
            createCard(Suit.CLUBS, Rank.SIX),
            createCard(Suit.DIAMONDS, Rank.FIVE)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(12, score);
    }

    @Test
    void testCalculateScoreMultipleAces() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.ACE),
            createCard(Suit.CLUBS, Rank.ACE),
            createCard(Suit.DIAMONDS, Rank.NINE)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(21, score);
    }

    @Test
    void testCalculateScoreBust() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.KING),
            createCard(Suit.CLUBS, Rank.SEVEN),
            createCard(Suit.DIAMONDS, Rank.SIX)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(23, score);
    }

    @Test
    void testBlackjackDetection() {
        List<CardEntity> hand = List.of(
            createCard(Suit.HEARTS, Rank.ACE),
            createCard(Suit.CLUBS, Rank.KING)
        );
        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(21, score);
        assertEquals(2, hand.size());
    }

    @Test
    void testHiddenCardNotCounted() {
        CardEntity hiddenCard = new CardEntity();
        hiddenCard.setSuit(Suit.HEARTS);
        hiddenCard.setRank(Rank.ACE);
        hiddenCard.setValue(11);
        hiddenCard.setIsHidden(true);

        List<CardEntity> hand = new ArrayList<>();
        hand.add(hiddenCard);
        hand.add(createCard(Suit.CLUBS, Rank.SEVEN));

        int score = scoreCalculator.calculateHandScore(hand);
        assertEquals(7, score);
    }

    @Test
    void testShouldDealerHit() {
        com.Ulises.BlackJackAPI.domain.entity.HandEntity hand = new com.Ulises.BlackJackAPI.domain.entity.HandEntity();
        hand.setScore(16);
        assertTrue(scoreCalculator.shouldDealerHit(hand));

        hand.setScore(17);
        assertFalse(scoreCalculator.shouldDealerHit(hand));
    }
}