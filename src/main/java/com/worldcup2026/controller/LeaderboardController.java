package com.worldcup2026.controller;

import com.worldcup2026.dto.LeaderboardEntryDTO;
import com.worldcup2026.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Slf4j
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard() {
        log.info("GET /api/leaderboard");

        List<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalBracketCount() {
        log.info("GET /api/leaderboard/count");
        
        long count = leaderboardService.getTotalBracketCount();
        return ResponseEntity.ok(count);
    }
}
