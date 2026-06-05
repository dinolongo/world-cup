package com.worldcup2026.client.footballdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballDataTeamResponse {

    private Integer id;
    private String name;
    private String shortName;
    private String tla;
    private String crest;

    @JsonProperty("area")
    private FootballDataArea area;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataArea {
        private String name;
    }
}
