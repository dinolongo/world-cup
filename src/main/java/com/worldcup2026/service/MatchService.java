package com.worldcup2026.service;

import com.worldcup2026.client.FootballDataClient;
import com.worldcup2026.client.footballdata.FootballDataMatchResponse;
import com.worldcup2026.config.CacheConfig;
import com.worldcup2026.dto.MatchDto;
import com.worldcup2026.dto.TeamDto;
import com.worldcup2026.entity.Match;
import com.worldcup2026.entity.Team;
import com.worldcup2026.exception.ResourceNotFoundException;
import com.worldcup2026.repository.MatchRepository;
import com.worldcup2026.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final FootballDataClient footballDataClient;

    @Value("${cache.ttl.matches:15m}")
    private Duration matchesCacheTtl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    @Cacheable(value = CacheConfig.MATCHES_CACHE, key = "'all'")
    public List<MatchDto> getAllMatches() {
        log.info("Fetching all matches from database");
        List<Match> matches = matchRepository.findAll();
        
        if (matches.isEmpty() || isDataStale(matches.get(0).getLastUpdated(), matchesCacheTtl)) {
            log.info("Matches data is stale or empty, refreshing from Football-Data.org");
            refreshMatchesFromApi();
            matches = matchRepository.findAll();
        }
        
        return matches.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Cacheable(value = CacheConfig.MATCHES_CACHE, key = "#id")
    public MatchDto getMatchById(Long id) {
        log.info("Fetching match by id: {}", id);
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        
        if (isDataStale(match.getLastUpdated(), matchesCacheTtl)) {
            log.info("Match data is stale, refreshing from Football-Data.org");
            refreshMatchFromApi(match.getExternalApiId());
            match = matchRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        }
        
        return mapToDto(match);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @CacheEvict(value = CacheConfig.MATCHES_CACHE, allEntries = true)
    public void refreshMatchesFromApi() {
        log.info("Refreshing matches from Football-Data.org API");
        try {
            List<FootballDataMatchResponse> apiMatches = footballDataClient.getMatches();
            
            Map<Integer, Team> teamMap = teamRepository.findAll().stream()
                    .collect(Collectors.toMap(Team::getExternalApiId, t -> t));
            
            for (FootballDataMatchResponse apiMatch : apiMatches) {
                matchRepository.findByExternalApiId(apiMatch.getId())
                        .ifPresentOrElse(
                                existingMatch -> updateMatch(existingMatch, apiMatch, teamMap),
                                () -> createMatch(apiMatch, teamMap)
                        );
            }
            
            log.info("Successfully refreshed {} matches", apiMatches.size());
        } catch (Exception e) {
            log.error("Failed to refresh matches from API", e);
            throw e;
        }
    }

    private void refreshMatchFromApi(Integer externalApiId) {
        log.info("Refreshing single match {} from Football-Data.org API", externalApiId);
        try {
            List<FootballDataMatchResponse> apiMatches = footballDataClient.getMatch(externalApiId);
            
            if (!apiMatches.isEmpty()) {
                FootballDataMatchResponse apiMatch = apiMatches.get(0);
                Map<Integer, Team> teamMap = teamRepository.findAll().stream()
                        .collect(Collectors.toMap(Team::getExternalApiId, t -> t));
                
                matchRepository.findByExternalApiId(apiMatch.getId())
                        .ifPresentOrElse(
                                existingMatch -> updateMatch(existingMatch, apiMatch, teamMap),
                                () -> createMatch(apiMatch, teamMap)
                        );
            }
        } catch (Exception e) {
            log.error("Failed to refresh match from API", e);
            throw e;
        }
    }

    private void createMatch(FootballDataMatchResponse apiMatch, Map<Integer, Team> teamMap) {
        Team homeTeam = teamMap.get(apiMatch.getHomeTeam().getId());
        Team awayTeam = teamMap.get(apiMatch.getAwayTeam().getId());
        
        if (homeTeam == null || awayTeam == null) {
            log.warn("Skipping match {} due to missing team data", apiMatch.getId());
            return;
        }
        
        Match match = Match.builder()
                .externalApiId(apiMatch.getId())
                .homeTeamId(homeTeam.getId())
                .awayTeamId(awayTeam.getId())
                .utcDate(parseUtcDate(apiMatch.getUtcDate()))
                .status(apiMatch.getMatchStatus())
                .homeScore(apiMatch.getScore() != null ? apiMatch.getScore().getHomeTeam() : null)
                .awayScore(apiMatch.getScore() != null ? apiMatch.getScore().getAwayTeam() : null)
                .duration(parseMatchDuration(apiMatch.getScore() != null ? apiMatch.getScore().getDuration() : null))
                .build();
        matchRepository.save(match);
        log.debug("Created new match: {} vs {}", homeTeam.getName(), awayTeam.getName());
    }

    private void updateMatch(Match existingMatch, FootballDataMatchResponse apiMatch, Map<Integer, Team> teamMap) {
        existingMatch.setUtcDate(parseUtcDate(apiMatch.getUtcDate()));
        existingMatch.setStatus(apiMatch.getMatchStatus());
        existingMatch.setHomeScore(apiMatch.getScore() != null ? apiMatch.getScore().getHomeTeam() : null);
        existingMatch.setAwayScore(apiMatch.getScore() != null ? apiMatch.getScore().getAwayTeam() : null);
        existingMatch.setDuration(parseMatchDuration(apiMatch.getScore() != null ? apiMatch.getScore().getDuration() : null));
        matchRepository.save(existingMatch);
        log.debug("Updated match: {}", existingMatch.getId());
    }

    private boolean isDataStale(LocalDateTime lastUpdated, Duration ttl) {
        if (lastUpdated == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(lastUpdated.plus(ttl));
    }

    private LocalDateTime parseUtcDate(String utcDate) {
        try {
            return LocalDateTime.parse(utcDate, DATE_FORMATTER);
        } catch (Exception e) {
            log.error("Failed to parse UTC date: {}", utcDate, e);
            return LocalDateTime.now();
        }
    }

    private Match.MatchDuration parseMatchDuration(String duration) {
        if (duration == null) {
            return null;
        }
        try {
            return Match.MatchDuration.valueOf(duration.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown duration value: {}, defaulting to REGULAR", duration);
            return Match.MatchDuration.REGULAR;
        }
    }

    private MatchDto mapToDto(Match match) {
        Team homeTeam = teamRepository.findById(match.getHomeTeamId()).orElse(null);
        Team awayTeam = teamRepository.findById(match.getAwayTeamId()).orElse(null);
        
        return MatchDto.builder()
                .id(match.getId())
                .externalApiId(match.getExternalApiId())
                .homeTeam(homeTeam != null ? mapTeamToDto(homeTeam) : null)
                .awayTeam(awayTeam != null ? mapTeamToDto(awayTeam) : null)
                .utcDate(match.getUtcDate())
                .status(match.getStatus())
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .duration(match.getDuration())
                .lastUpdated(match.getLastUpdated())
                .build();
    }

    private TeamDto mapTeamToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .externalApiId(team.getExternalApiId())
                .name(team.getName())
                .shortName(team.getShortName())
                .tla(team.getTla())
                .crestUrl(team.getCrestUrl())
                .build();
    }
}
