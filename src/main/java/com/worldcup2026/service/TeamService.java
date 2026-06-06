package com.worldcup2026.service;

import com.worldcup2026.client.FootballDataClient;
import com.worldcup2026.client.footballdata.FootballDataTeamResponse;
import com.worldcup2026.config.CacheConfig;
import com.worldcup2026.dto.TeamDto;
import com.worldcup2026.entity.Team;
import com.worldcup2026.exception.ResourceNotFoundException;
import com.worldcup2026.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final FootballDataClient footballDataClient;

    @Value("${cache.ttl.teams:24h}")
    private Duration teamsCacheTtl;

    @Transactional
    @Cacheable(value = CacheConfig.TEAMS_CACHE, key = "'all'")
    public List<TeamDto> getAllTeams() {
        log.info("Fetching all teams from database");
        List<Team> teams = teamRepository.findAll();
        
        if (teams.isEmpty()) {
            log.info("No teams found in database, attempting to refresh from Football-Data.org");
            refreshTeamsFromApi();
            teams = teamRepository.findAll();
        }
        
        return teams.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Cacheable(value = CacheConfig.TEAMS_CACHE, key = "#id")
    public TeamDto getTeamById(Long id) {
        log.info("Fetching team by id: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        
        return mapToDto(team);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @CacheEvict(value = CacheConfig.TEAMS_CACHE, allEntries = true)
    public void refreshTeamsFromApi() {
        log.info("Refreshing teams from Football-Data.org API");
        try {
            List<FootballDataTeamResponse> apiTeams = footballDataClient.getTeams();
            
            for (FootballDataTeamResponse apiTeam : apiTeams) {
                teamRepository.findByExternalApiId(apiTeam.getId())
                        .ifPresentOrElse(
                                existingTeam -> updateTeam(existingTeam, apiTeam),
                                () -> createTeam(apiTeam)
                        );
            }
            
            log.info("Successfully refreshed {} teams", apiTeams.size());
        } catch (Exception e) {
            log.error("Failed to refresh teams from API", e);
            throw e;
        }
    }

    private void createTeam(FootballDataTeamResponse apiTeam) {
        Team team = Team.builder()
                .externalApiId(apiTeam.getId())
                .name(apiTeam.getName())
                .shortName(apiTeam.getShortName())
                .tla(apiTeam.getTla())
                .crestUrl(apiTeam.getCrest())
                .build();
        teamRepository.save(team);
        log.debug("Created new team: {}", team.getName());
    }

    private void updateTeam(Team existingTeam, FootballDataTeamResponse apiTeam) {
        existingTeam.setName(apiTeam.getName());
        existingTeam.setShortName(apiTeam.getShortName());
        existingTeam.setTla(apiTeam.getTla());
        existingTeam.setCrestUrl(apiTeam.getCrest());
        teamRepository.save(existingTeam);
        log.debug("Updated team: {}", existingTeam.getName());
    }

    private TeamDto mapToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .externalApiId(team.getExternalApiId())
                .name(team.getName())
                .shortName(team.getShortName())
                .tla(team.getTla())
                .crestUrl(team.getCrestUrl())
                .lastUpdated(team.getUpdatedAt())
                .build();
    }
}
