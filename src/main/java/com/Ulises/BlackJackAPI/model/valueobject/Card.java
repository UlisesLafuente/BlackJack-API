package com.Ulises.BlackJackAPI.model.valueobject;

import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;

public class Card {
    private final Suit suit;
    private final Rank rank;
    private final int value;
    private boolean isHidden;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = rank.getValue();
        this.isHidden = false;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    @Override
    public String toString() {
        return rank.name() + "_" + suit.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        int result = suit.hashCode();
        result = 31 * result + rank.hashCode();
        return result;
    }
}