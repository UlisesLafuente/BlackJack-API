package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for player ranking responses.
 * Contains a list of players ordered by score.
 *
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class RankingResponse {
    private List<PlayerResponse> rankings;

    public RankingResponse() {
    }

    public RankingResponse(List<PlayerResponse> rankings) {
        this.rankings = rankings;
    }

}