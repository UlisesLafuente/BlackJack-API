package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardEntityCreation() {
        CardEntity card = new CardEntity();
        card.setSuit(Suit.HEARTS);
        card.setRank(Rank.ACE);
        card.setValue(11);
        card.setIsHidden(false);

        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(11, card.getValue());
        assertFalse(card.getIsHidden());
    }

    @Test
    void testAceCard() {
        CardEntity aceCard = new CardEntity();
        aceCard.setSuit(Suit.SPADES);
        aceCard.setRank(Rank.ACE);
        aceCard.setValue(11);

        assertEquals(Rank.ACE, aceCard.getRank());
        assertEquals(11, aceCard.getValue());
    }

    @Test
    void testNumberCards() {
        CardEntity twoCard = new CardEntity();
        twoCard.setRank(Rank.TWO);
        twoCard.setValue(2);
        assertEquals(2, twoCard.getValue());

        CardEntity fiveCard = new CardEntity();
        fiveCard.setRank(Rank.FIVE);
        fiveCard.setValue(5);
        assertEquals(5, fiveCard.getValue());

        CardEntity nineCard = new CardEntity();
        nineCard.setRank(Rank.NINE);
        nineCard.setValue(9);
        assertEquals(9, nineCard.getValue());
    }

    @Test
    void testFaceCards() {
        CardEntity jackCard = new CardEntity();
        jackCard.setRank(Rank.JACK);
        jackCard.setValue(10);
        assertEquals(10, jackCard.getValue());

        CardEntity queenCard = new CardEntity();
        queenCard.setRank(Rank.QUEEN);
        queenCard.setValue(10);
        assertEquals(10, queenCard.getValue());

        CardEntity kingCard = new CardEntity();
        kingCard.setRank(Rank.KING);
        kingCard.setValue(10);
        assertEquals(10, kingCard.getValue());
    }

    @Test
    void testCardHidden() {
        CardEntity card = new CardEntity();
        card.setSuit(Suit.HEARTS);
        card.setRank(Rank.KING);
        card.setValue(10);
        card.setIsHidden(false);
        assertFalse(card.getIsHidden());

        card.setIsHidden(true);
        assertTrue(card.getIsHidden());
    }

    @Test
    void testCardEntityEquality() {
        CardEntity card1 = new CardEntity();
        card1.setSuit(Suit.HEARTS);
        card1.setRank(Rank.ACE);
        card1.setValue(11);
        card1.setIsHidden(false);

        CardEntity card2 = new CardEntity();
        card2.setSuit(Suit.HEARTS);
        card2.setRank(Rank.ACE);
        card2.setValue(11);
        card2.setIsHidden(false);

        assertNotEquals(card1, card2);
        assertNotEquals(card1.hashCode(), card2.hashCode());
    }
}