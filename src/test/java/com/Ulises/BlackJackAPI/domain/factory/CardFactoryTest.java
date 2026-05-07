package com.Ulises.BlackJackAPI.domain.factory;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.service.DeckService.DrawnCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardFactoryTest {

    private CardFactory cardFactory;
    private HandEntity hand;

    @BeforeEach
    void setUp() {
        cardFactory = new CardFactory();
        hand = new HandEntity(1L, HandType.PLAYER, 0);
    }

    @Test
    void testCreateCardEntityWithSuitAndRank() {
        CardEntity card = cardFactory.createCardEntity(hand, Suit.HEARTS, Rank.ACE, false);

        assertNotNull(card);
        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(11, card.getValue());
        assertFalse(card.getIsHidden());
    }

    @Test
    void testCreateCardEntityHidden() {
        CardEntity card = cardFactory.createCardEntity(hand, Suit.SPADES, Rank.KING, true);

        assertTrue(card.getIsHidden());
    }

    @Test
    void testCreateCardEntityFromDrawnCard() {
        DrawnCard drawnCard = new DrawnCard(Suit.DIAMONDS, Rank.SEVEN);
        CardEntity card = cardFactory.createCardEntity(hand, drawnCard, false);

        assertNotNull(card);
        assertEquals(Suit.DIAMONDS, card.getSuit());
        assertEquals(Rank.SEVEN, card.getRank());
        assertEquals(7, card.getValue());
    }

    @Test
    void testFaceCardsHaveValue10() {
        for (Rank rank : new Rank[]{Rank.JACK, Rank.QUEEN, Rank.KING}) {
            CardEntity card = cardFactory.createCardEntity(hand, Suit.CLUBS, rank, false);
            assertEquals(10, card.getValue(), rank.name() + " should have value 10");
        }
    }

    @Test
    void testAceHasValue11() {
        CardEntity card = cardFactory.createCardEntity(hand, Suit.HEARTS, Rank.ACE, false);
        assertEquals(11, card.getValue());
    }
}