package com.Ulises.BlackJackAPI.dto;

import java.util.List;

public class RankingResponse {
    private List<PlayerResponse> rankings;

    public RankingResponse() {}

    public RankingResponse(List<PlayerResponse> rankings) {
        this.rankings = rankings;
    }

    public List<PlayerResponse> getRankings() { return rankings; }
    public void setRankings(List<PlayerResponse> rankings) { this.rankings = rankings; }
}