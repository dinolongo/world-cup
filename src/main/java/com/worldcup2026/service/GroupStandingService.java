package com.worldcup2026.service;

import com.worldcup2026.client.FootballDataClient;
import com.worldcup2026.client.footballdata.FootballDataStandingResponse;
import com.worldcup2026.config.CacheConfig;
import com.worldcup2026.dto.GroupStandingDto;
import com.worldcup2026.dto.TeamDto;
import com.worldcup2026.entity.GroupStanding;
import com.worldcup2026.entity.Team;
import com.worldcup2026.exception.ResourceNotFoundException;
import com.worldcup2026.repository.GroupStandingRepository;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupStandingService {

    private final GroupStandingRepository groupStandingRepository;
    private final TeamRepository teamRepository;
    private final FootballDataClient footballDataClient;

    @Value("${cache.ttl.standings:1h}")
    private Duration standingsCacheTtl;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.STANDINGS_CACHE, key = "'all'")
    public List<GroupStandingDto> getAllGroupStandings() {
        log.info("Fetching all group standings from database");
        List<GroupStanding> standings = groupStandingRepository.findAll();
        
        if (standings.isEmpty() || isDataStale(standings.get(0).getLastUpdated(), standingsCacheTtl)) {
            log.info("Standings data is stale or empty, refreshing from Football-Data.org");
            refreshStandingsFromApi();
            standings = groupStandingRepository.findAll();
        }
        
        return standings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.STANDINGS_CACHE, key = "#groupName")
    public List<GroupStandingDto> getGroupStandingsByGroup(String groupName) {
        log.info("Fetching standings for group: {}", groupName);
        List<GroupStanding> standings = groupStandingRepository.findByGroupNameOrderByPositionAsc(groupName);
        
        if (standings.isEmpty() || isDataStale(standings.get(0).getLastUpdated(), standingsCacheTtl)) {
            log.info("Standings data for group {} is stale or empty, refreshing from Football-Data.org", groupName);
            refreshStandingsFromApi();
            standings = groupStandingRepository.findByGroupNameOrderByPositionAsc(groupName);
        }
        
        return standings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @CacheEvict(value = CacheConfig.STANDINGS_CACHE, allEntries = true)
    public void refreshStandingsFromApi() {
        log.info("Refreshing standings from Football-Data.org API");
        try {
            List<List<FootballDataStandingResponse>> apiStandings = footballDataClient.getStandings();
            
            Map<Integer, Team> teamMap = teamRepository.findAll().stream()
                    .collect(Collectors.toMap(Team::getExternalApiId, t -> t));
            
            for (List<FootballDataStandingResponse> groupStandings : apiStandings) {
                String groupName = extractGroupName(groupStandings);
                
                if (groupName != null) {
                    groupStandingRepository.deleteByGroupName(groupName);
                    
                    for (FootballDataStandingResponse apiStanding : groupStandings) {
                        createStanding(apiStanding, groupName, teamMap);
                    }
                }
            }
            
            log.info("Successfully refreshed standings for {} groups", apiStandings.size());
        } catch (Exception e) {
            log.error("Failed to refresh standings from API", e);
            throw e;
        }
    }

    private String extractGroupName(List<FootballDataStandingResponse> groupStandings) {
        if (groupStandings.isEmpty() || groupStandings.get(0).getTeam() == null) {
            return null;
        }
        
        String teamName = groupStandings.get(0).getTeam().getName();
        if (teamName != null && teamName.contains("Group")) {
            int groupIndex = teamName.indexOf("Group");
            return teamName.substring(groupIndex).trim();
        }
        
        return "GROUP_" + System.currentTimeMillis();
    }

    private void createStanding(FootballDataStandingResponse apiStanding, String groupName, Map<Integer, Team> teamMap) {
        Team team = teamMap.get(apiStanding.getTeam().getId());
        
        if (team == null) {
            log.warn("Skipping standing for team {} due to missing team data", apiStanding.getTeam().getId());
            return;
        }
        
        GroupStanding standing = GroupStanding.builder()
                .groupName(groupName)
                .teamId(team.getId())
                .position(apiStanding.getPosition())
                .playedGames(apiStanding.getPlayedGames())
                .wins(apiStanding.getWins())
                .draws(apiStanding.getDraws())
                .losses(apiStanding.getLosses())
                .goalsFor(apiStanding.getGoalsFor())
                .goalsAgainst(apiStanding.getGoalsAgainst())
                .goalDifference(apiStanding.getGoalDifference())
                .points(apiStanding.getPoints())
                .build();
        groupStandingRepository.save(standing);
        log.debug("Created standing for team {} in group {}", team.getName(), groupName);
    }

    private boolean isDataStale(LocalDateTime lastUpdated, Duration ttl) {
        if (lastUpdated == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(lastUpdated.plus(ttl));
    }

    private GroupStandingDto mapToDto(GroupStanding standing) {
        Team team = teamRepository.findById(standing.getTeamId()).orElse(null);
        
        return GroupStandingDto.builder()
                .id(standing.getId())
                .groupName(standing.getGroupName())
                .team(team != null ? mapTeamToDto(team) : null)
                .position(standing.getPosition())
                .playedGames(standing.getPlayedGames())
                .wins(standing.getWins())
                .draws(standing.getDraws())
                .losses(standing.getLosses())
                .goalsFor(standing.getGoalsFor())
                .goalsAgainst(standing.getGoalsAgainst())
                .goalDifference(standing.getGoalDifference())
                .points(standing.getPoints())
                .lastUpdated(standing.getLastUpdated())
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
