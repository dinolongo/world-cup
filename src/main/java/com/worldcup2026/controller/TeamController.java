package com.worldcup2026.controller;

import com.worldcup2026.dto.TeamDto;
import com.worldcup2026.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teams", description = "World Cup team endpoints")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "Get all teams", description = "Retrieve all World Cup teams with caching")
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        log.info("GET /api/teams");
        List<TeamDto> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Retrieve a specific team by its database ID")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        log.info("GET /api/teams/{}", id);
        TeamDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }
}
