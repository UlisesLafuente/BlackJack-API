package com.Ulises.BlackJackAPI.domain.factory;

import com.Ulises.BlackJackAPI.domain.valueobject.Card;
import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import org.springframework.stereotype.Component;

@Component
public class CardFactory {

    public Card createCard(Suit suit, Rank rank, boolean hidden) {
        Card card = new Card(suit, rank);
        card.setHidden(hidden);
        return card;
    }

    public CardEntity createCardEntity(HandEntity hand, Suit suit, Rank rank, boolean hidden) {
        return new CardEntity(hand.getId(), suit, rank, hidden);
    }

    public Card toDomainCard(CardEntity entity) {
        Card card = new Card(entity.getSuit(), entity.getRank());
        card.setHidden(Boolean.TRUE.equals(entity.getIsHidden()));
        return card;
    }
}