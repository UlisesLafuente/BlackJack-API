package com.Ulises.BlackJackAPI.domain.factory;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import com.Ulises.BlackJackAPI.service.DeckService.DrawnCard;
import org.springframework.stereotype.Component;

/**
 * Factory for creating card entities.
 * Handles conversion from DrawnCard to CardEntity.
 *
 * @author Ulises Lafuente
 */
@Component
public class CardFactory {

    public CardEntity createCardEntity(HandEntity hand, Suit suit, Rank rank, boolean hidden) {
        return new CardEntity(hand.getId(), suit, rank, hidden);
    }

    public CardEntity createCardEntity(HandEntity hand, DrawnCard drawnCard, boolean hidden) {
        return new CardEntity(hand.getId(), drawnCard.suit(), drawnCard.rank(), hidden);
    }
}