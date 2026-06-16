package com.worldcup2026.controller;

import com.worldcup2026.dto.MatchDto;
import com.worldcup2026.service.MatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Matches", description = "World Cup match endpoints")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchDto>> getAllMatches() {
        log.info("GET /api/matches");
        List<MatchDto> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDto> getMatchById(@PathVariable Long id) {
        log.info("GET /api/matches/{}", id);
        MatchDto match = matchService.getMatchById(id);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/refresh")
    public void refreshMatches() {
        log.info("GET /api/matches/refresh");
        matchService.refreshMatchesFromApi();
    }
}
