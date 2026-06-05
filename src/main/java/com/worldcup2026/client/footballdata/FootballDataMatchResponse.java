package com.worldcup2026.client.footballdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worldcup2026.entity.Match;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballDataMatchResponse {

    private Integer id;
    private String status;
    private String utcDate;

    @JsonProperty("homeTeam")
    private FootballDataTeamRef homeTeam;

    @JsonProperty("awayTeam")
    private FootballDataTeamRef awayTeam;

    private FootballDataScore score;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataTeamRef {
        private Integer id;
        private String name;
        private String shortName;
        private String tla;
        private String crest;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataScore {
        @JsonProperty("homeTeam")
        private Integer homeTeam;

        @JsonProperty("awayTeam")
        private Integer awayTeam;
    }

    public Match.MatchStatus getMatchStatus() {
        try {
            return Match.MatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Match.MatchStatus.TIMED;
        }
    }
}
