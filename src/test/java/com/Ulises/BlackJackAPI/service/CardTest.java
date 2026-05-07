package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;
import com.Ulises.BlackJackAPI.model.valueobject.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);

        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(11, card.getValue());
        assertFalse(card.isHidden());
    }

    @Test
    void testAceCard() {
        Card aceCard = new Card(Suit.SPADES, Rank.ACE);
        assertTrue(aceCard.isAce());
        assertEquals(11, aceCard.getValue());
    }

    @Test
    void testNumberCards() {
        Card twoCard = new Card(Suit.CLUBS, Rank.TWO);
        assertEquals(2, twoCard.getValue());

        Card fiveCard = new Card(Suit.DIAMONDS, Rank.FIVE);
        assertEquals(5, fiveCard.getValue());

        Card nineCard = new Card(Suit.HEARTS, Rank.NINE);
        assertEquals(9, nineCard.getValue());
    }

    @Test
    void testFaceCards() {
        Card jackCard = new Card(Suit.CLUBS, Rank.JACK);
        assertEquals(10, jackCard.getValue());

        Card queenCard = new Card(Suit.DIAMONDS, Rank.QUEEN);
        assertEquals(10, queenCard.getValue());

        Card kingCard = new Card(Suit.SPADES, Rank.KING);
        assertEquals(10, kingCard.getValue());
    }

    @Test
    void testCardHidden() {
        Card card = new Card(Suit.HEARTS, Rank.KING);
        assertFalse(card.isHidden());

        card.setHidden(true);
        assertTrue(card.isHidden());
    }

    @Test
    void testCardToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("ACE_HEARTS", card.toString());
    }

    @Test
    void testCardEquality() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.ACE);
        Card card3 = new Card(Suit.SPADES, Rank.ACE);

        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
    }
}