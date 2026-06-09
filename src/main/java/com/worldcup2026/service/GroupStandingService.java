package com.worldcup2026.service;

import com.worldcup2026.dto.GroupStandingDto;
import com.worldcup2026.entity.Match;
import com.worldcup2026.entity.Team;
import com.worldcup2026.repository.MatchRepository;
import com.worldcup2026.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupStandingService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public List<GroupStandingDto> getAllGroupStandings() {
        log.info("Calculating all group standings from match data");
        
        // Get all unique group names from teams
        List<String> groupNames = teamRepository.findAll().stream()
                .map(Team::getGroupName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Calculate standings for each group
        List<GroupStandingDto> allStandings = new ArrayList<>();
        for (String groupName : groupNames) {
            allStandings.addAll(calculateGroupStandings(groupName));
        }
        
        return allStandings;
    }

    public List<GroupStandingDto> getGroupStandingsByGroup(String groupName) {
        log.info("Calculating standings for group: {}", groupName);
        return calculateGroupStandings(groupName);
    }

    private List<GroupStandingDto> calculateGroupStandings(String groupName) {
        // Get all teams in this group
        List<Team> teams = teamRepository.findByGroupName(groupName);
        if (teams.isEmpty()) {
            log.warn("No teams found for group: {}", groupName);
            return List.of();
        }
        
        // Get all group stage matches for this group
        List<Match> matches = matchRepository.findByStageAndGroup("GROUP_STAGE", groupName);
        
        // Calculate standings for each team
        Map<Long, TeamStats> teamStatsMap = new HashMap<>();
        
        for (Team team : teams) {
            teamStatsMap.put(team.getId(), new TeamStats(team.getId(), team.getName()));
        }
        
        // Process each match
        for (Match match : matches) {
            if (match.getStatus() == Match.MatchStatus.FINISHED && 
                match.getHomeScore() != null && match.getAwayScore() != null) {
                
                TeamStats homeStats = teamStatsMap.get(match.getHomeTeamId());
                TeamStats awayStats = teamStatsMap.get(match.getAwayTeamId());
                
                if (homeStats != null && awayStats != null) {
                    homeStats.played++;
                    awayStats.played++;
                    
                    homeStats.goalsFor += match.getHomeScore();
                    homeStats.goalsAgainst += match.getAwayScore();
                    
                    awayStats.goalsFor += match.getAwayScore();
                    awayStats.goalsAgainst += match.getHomeScore();
                    
                    if (match.getHomeScore() > match.getAwayScore()) {
                        homeStats.wins++;
                        homeStats.points += 3;
                        awayStats.losses++;
                    } else if (match.getHomeScore() < match.getAwayScore()) {
                        awayStats.wins++;
                        awayStats.points += 3;
                        homeStats.losses++;
                    } else {
                        homeStats.draws++;
                        awayStats.draws++;
                        homeStats.points += 1;
                        awayStats.points += 1;
                    }
                }
            }
        }
        
        // Convert to DTOs and sort
        return teamStatsMap.values().stream()
                .map(stats -> GroupStandingDto.builder()
                        .groupName(groupName)
                        .teamId(String.valueOf(stats.teamId))
                        .teamName(stats.teamName)
                        .played(stats.played)
                        .wins(stats.wins)
                        .draws(stats.draws)
                        .losses(stats.losses)
                        .goalsFor(stats.goalsFor)
                        .goalsAgainst(stats.goalsAgainst)
                        .goalDifference(stats.goalsFor - stats.goalsAgainst)
                        .points(stats.points)
                        .build())
                .sorted((a, b) -> {
                    // Sort by points (desc), then goal difference (desc), then goals for (desc)
                    if (b.getPoints() != a.getPoints()) {
                        return Integer.compare(b.getPoints(), a.getPoints());
                    }
                    if (b.getGoalDifference() != a.getGoalDifference()) {
                        return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
                    }
                    return Integer.compare(b.getGoalsFor(), a.getGoalsFor());
                })
                .collect(Collectors.toList());
    }

    private static class TeamStats {
        Long teamId;
        String teamName;
        int played = 0;
        int wins = 0;
        int draws = 0;
        int losses = 0;
        int goalsFor = 0;
        int goalsAgainst = 0;
        int points = 0;

        TeamStats(Long teamId, String teamName) {
            this.teamId = teamId;
            this.teamName = teamName;
        }
    }
}
