package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.domain.enums.HandType;
import lombok.Getter;

import java.util.List;

/**
 * DTO for hand responses.
 * Contains hand details including type, score, and cards.
 *
 * @author Ulises Lafuente
 */
@Getter
public class HandResponse {
    private Long id;
    private HandType type;
    private Integer handIndex;
    private Integer score;
    private List<CardResponse> cards;

    public HandResponse() {
    }

    public HandResponse(Long id, HandType type, Integer handIndex, Integer score, List<CardResponse> cards) {
        this.id = id;
        this.type = type;
        this.handIndex = handIndex;
        this.score = score;
        this.cards = cards;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(HandType type) {
        this.type = type;
    }

    public void setHandIndex(Integer handIndex) {
        this.handIndex = handIndex;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCards(List<CardResponse> cards) {
        this.cards = cards;
    }
}