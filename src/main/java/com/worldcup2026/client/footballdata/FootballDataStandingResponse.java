package com.worldcup2026.client.footballdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballDataStandingResponse {

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("team")
    private FootballDataTeamRef team;

    @JsonProperty("playedGames")
    private Integer playedGames;

    @JsonProperty("won")
    private Integer wins;

    @JsonProperty("draw")
    private Integer draws;

    @JsonProperty("lost")
    private Integer losses;

    @JsonProperty("goalsFor")
    private Integer goalsFor;

    @JsonProperty("goalsAgainst")
    private Integer goalsAgainst;

    @JsonProperty("goalDifference")
    private Integer goalDifference;

    @JsonProperty("points")
    private Integer points;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataTeamRef {
        private Integer id;
        private String name;
        private String shortName;
        private String tla;
        private String crest;
    }
}
