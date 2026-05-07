package com.Ulises.BlackJackAPI.domain.factory;

import com.Ulises.BlackJackAPI.domain.game.Card;
import com.Ulises.BlackJackAPI.domain.game.Hand;
import com.Ulises.BlackJackAPI.model.entity.CardEntity;
import com.Ulises.BlackJackAPI.model.entity.HandEntity;
import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;
import org.springframework.stereotype.Component;

@Component
public class CardFactory {

    public Card createCard(Suit suit, Rank rank, boolean hidden) {
        return new Card(suit, rank, hidden);
    }

    public Card createCard(Long handId, Suit suit, Rank rank, boolean hidden) {
        return new Card(handId, suit, rank, hidden);
    }

    public CardEntity createCardEntity(Hand hand, Suit suit, Rank rank, boolean hidden) {
        return new CardEntity(hand.getId(), suit, rank, hidden);
    }

    public CardEntity createCardEntity(HandEntity hand, Suit suit, Rank rank, boolean hidden) {
        return new CardEntity(hand.getId(), suit, rank, hidden);
    }

    public Hand createHand(Long gameId, com.Ulises.BlackJackAPI.model.enums.HandType type, Integer handIndex) {
        return new Hand(gameId, type, handIndex);
    }

    public HandEntity createHandEntity(Long gameId, com.Ulises.BlackJackAPI.model.enums.HandType type, Integer handIndex) {
        return new HandEntity(gameId, type, handIndex);
    }

    public Card toDomainCard(CardEntity entity) {
        Card card = new Card(entity.getSuit(), entity.getRank(), entity.getIsHidden());
        card.setId(entity.getId());
        card.setHandId(entity.getHandId());
        return card;
    }

    public CardEntity toEntity(Card card) {
        CardEntity entity = new CardEntity();
        entity.setId(card.getId());
        entity.setHandId(card.getHandId());
        entity.setSuit(card.getSuit());
        entity.setRank(card.getRank());
        entity.setValue(card.getValue());
        entity.setIsHidden(card.isHidden());
        return entity;
    }

    public Hand toDomainHand(HandEntity entity) {
        Hand hand = new Hand(entity.getGameId(), entity.getType(), entity.getHandIndex());
        hand.setId(entity.getId());
        hand.setScore(entity.getScore());
        return hand;
    }

    public HandEntity toEntity(Hand hand) {
        HandEntity entity = new HandEntity();
        entity.setId(hand.getId());
        entity.setGameId(hand.getGameId());
        entity.setType(hand.getType());
        entity.setHandIndex(hand.getHandIndex());
        entity.setScore(hand.getScore());
        return entity;
    }
}