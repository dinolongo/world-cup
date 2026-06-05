package com.worldcup2026.client.footballdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballDataMatchesResponse {

    @JsonProperty("matches")
    private List<FootballDataMatchResponse> matches;
}
