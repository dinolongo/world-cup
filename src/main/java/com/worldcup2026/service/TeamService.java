package com.worldcup2026.service;

import com.worldcup2026.config.CacheConfig;
import com.worldcup2026.dto.TeamDto;
import com.worldcup2026.entity.Team;
import com.worldcup2026.exception.ResourceNotFoundException;
import com.worldcup2026.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    @Transactional
    @Cacheable(value = CacheConfig.TEAMS_CACHE, key = "'all'")
    public List<TeamDto> getAllTeams() {
        log.info("Fetching all teams from database");
        List<Team> teams = teamRepository.findAll();
        
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
