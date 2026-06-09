package com.worldcup2026.client;

import com.worldcup2026.client.footballdata.FootballDataMatchResponse;
import com.worldcup2026.client.footballdata.FootballDataMatchesResponse;
import com.worldcup2026.exception.FootballDataApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FootballDataClient {

    private final RestClient restClient;

    @Value("${football-data.api.base-url}")
    private String baseUrl;

    @Value("${football-data.api.competitions.world-cup}")
    private String worldCupCode;

    @Value("${football-data.api.api-key}")
    private String apiKey;

    public List<FootballDataMatchResponse> getMatches() {
        log.info("Fetching matches from Football-Data.org API");
        try {
            FootballDataMatchesResponse response = restClient.get()
                    .uri(baseUrl + "/competitions/{code}/matches", worldCupCode)
                    .header("X-Auth-Token", apiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FootballDataMatchesResponse.class);

            return response != null ? response.getMatches() : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching matches from Football-Data.org: {}", e.getMessage());
            throw new FootballDataApiException("Failed to fetch matches from Football-Data.org", e);
        }
    }

    public List<FootballDataMatchResponse> getMatch(Integer matchId) {
        log.info("Fetching match {} from Football-Data.org API", matchId);
        try {
            FootballDataMatchResponse response = restClient.get()
                    .uri(baseUrl + "/matches/{matchId}", matchId)
                    .header("X-Auth-Token", apiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FootballDataMatchResponse.class);

            return response != null ? List.of(response) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching match {} from Football-Data.org: {}", matchId, e.getMessage());
            throw new FootballDataApiException("Failed to fetch match from Football-Data.org", e);
        }
    }
}
