package com.worldcup2026.controller;

import com.worldcup2026.dto.LeaderboardEntryDTO;
import com.worldcup2026.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leaderboard", description = "World Cup predictions leaderboard endpoints")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get paginated leaderboard")
    public ResponseEntity<Page<LeaderboardEntryDTO>> getLeaderboard(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/leaderboard - page: {}, size: {}", page, size);

        // Sort by total_score DESC, then created_at ASC as tiebreaker
        Sort sort = Sort.by(
                Sort.Order.desc("totalScore"),
                Sort.Order.asc("createdAt")
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard(pageable);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/count")
    @Operation(summary = "Get total bracket count")
    public ResponseEntity<Long> getTotalBracketCount() {
        log.info("GET /api/leaderboard/count");
        
        long count = leaderboardService.getTotalBracketCount();
        return ResponseEntity.ok(count);
    }
}
