package com.Ulises.BlackJackAPI.model.entity;

import com.Ulises.BlackJackAPI.model.enums.HandType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("hands")
public class HandEntity {
    @Id
    private Long id;

    @Column("game_id")
    private Long gameId;

    @Column("type")
    private HandType type;

    @Column("hand_index")
    private Integer handIndex;

    @Column("score")
    private Integer score;

    public HandEntity() {}

    public HandEntity(Long gameId, HandType type, Integer handIndex) {
        this.gameId = gameId;
        this.type = type;
        this.handIndex = handIndex;
        this.score = 0;
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
}