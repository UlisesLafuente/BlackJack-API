package com.Ulises.BlackJackAPI.domain.services;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesEngineTest {

    private GameRulesEngine rulesEngine;

    @BeforeEach
    void setUp() {
        rulesEngine = new GameRulesEngine();
    }

    @Test
    void testCheckBlackjack() {
        HandEntity hand = new HandEntity();
        hand.setScore(21);
        assertTrue(rulesEngine.checkBlackjack(hand));

        hand.setScore(20);
        assertFalse(rulesEngine.checkBlackjack(hand));
    }

    @Test
    void testCanDealerShowCard() {
        List<HandEntity> hands = new ArrayList<>();
        hands.add(new HandEntity(1L, HandType.CROUPIER, 0));

        assertFalse(rulesEngine.canDealerShowCard(hands));

        hands.add(new HandEntity(1L, HandType.PLAYER, 0));
        assertTrue(rulesEngine.canDealerShowCard(hands));
    }

    @Test
    void testDetermineResultPlayerWins() {
        assertEquals(GameResult.WIN, rulesEngine.determineResult(20, 18));
        assertEquals(GameResult.WIN, rulesEngine.determineResult(21, 20));
    }

    @Test
    void testDetermineResultDealerWins() {
        assertEquals(GameResult.LOSE, rulesEngine.determineResult(18, 20));
        assertEquals(GameResult.LOSE, rulesEngine.determineResult(17, 19));
    }

    @Test
    void testDetermineResultPlayerBust() {
        assertEquals(GameResult.PLAYER_BUST, rulesEngine.determineResult(22, 15));
    }

    @Test
    void testDetermineResultDealerBust() {
        assertEquals(GameResult.WIN, rulesEngine.determineResult(18, 25));
    }

    @Test
    void testDetermineResultPush() {
        assertEquals(GameResult.PUSH, rulesEngine.determineResult(20, 20));
        assertEquals(GameResult.PUSH, rulesEngine.determineResult(17, 17));
    }

    @Test
    void testCalculateScoreChangeWin() {
        assertEquals(15, rulesEngine.calculateScoreChange(GameResult.WIN, 10, 0));
    }

    @Test
    void testCalculateScoreChangeLose() {
        assertEquals(-10, rulesEngine.calculateScoreChange(GameResult.LOSE, 10, 0));
    }

    @Test
    void testCalculateScoreChangePush() {
        assertEquals(0, rulesEngine.calculateScoreChange(GameResult.PUSH, 10, 0));
    }

    @Test
    void testCalculateScoreChangeWithBlackjack() {
        assertEquals(15, rulesEngine.calculateScoreChange(GameResult.BLACKJACK, 10, 0));
    }

    @Test
    void testCalculateScoreChangeWithInsurance() {
        assertEquals(20, rulesEngine.calculateScoreChange(GameResult.INSURANCE_WIN, 10, 10));
        assertEquals(-10, rulesEngine.calculateScoreChange(GameResult.INSURANCE_LOSE, 10, 10));
    }

    @Test
    void testIsFirstDealerCardAce() {
        List<CardEntity> cards = new ArrayList<>();
        CardEntity aceCard = new CardEntity();
        aceCard.setRank(Rank.ACE);
        cards.add(aceCard);

        assertTrue(rulesEngine.isFirstDealerCardAce(cards));
    }

    @Test
    void testIsFirstDealerCardNotAce() {
        List<CardEntity> cards = new ArrayList<>();
        CardEntity kingCard = new CardEntity();
        kingCard.setRank(Rank.KING);
        cards.add(kingCard);

        assertFalse(rulesEngine.isFirstDealerCardAce(cards));
    }

    @Test
    void testIsFirstDealerCardAceEmptyList() {
        assertFalse(rulesEngine.isFirstDealerCardAce(new ArrayList<>()));
    }
}