package com.Ulises.BlackJackAPI.domain.game;

import com.Ulises.BlackJackAPI.model.enums.HandType;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private Long id;
    private Long gameId;
    private HandType type;
    private Integer handIndex;
    private Integer score;
    private List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public Hand(Long gameId, HandType type, Integer handIndex) {
        this.gameId = gameId;
        this.type = type;
        this.handIndex = handIndex;
        this.score = 0;
        this.cards = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public HandType getType() { return type; }
    public void setType(HandType type) { this.type = type; }
    public Integer getHandIndex() { return handIndex; }
    public void setHandIndex(Integer handIndex) { this.handIndex = handIndex; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }

    public void addCard(Card card) {
        this.cards.add(card);
        this.score = calculateScore();
    }

    public boolean isBusted() {
        return this.score > 21;
    }

    public boolean isBlackjack() {
        return this.score == 21 && this.cards.size() == 2;
    }

    public boolean canSplit() {
        return this.type == HandType.PLAYER &&
               this.cards.size() == 2 &&
               this.cards.get(0).getRank() == this.cards.get(1).getRank();
    }

    private int calculateScore() {
        int score = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.isHidden()) continue;
            score += card.getValue();
            if (card.isAce()) aces++;
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }
}