package com.worldcup2026.controller;

import com.worldcup2026.dto.GroupStandingDto;
import com.worldcup2026.service.GroupStandingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Groups", description = "World Cup group standings endpoints")
public class GroupController {

    private final GroupStandingService groupStandingService;

    @GetMapping
    @Operation(summary = "Get all group standings", description = "Retrieve all World Cup group standings calculated from match data")
    public ResponseEntity<List<GroupStandingDto>> getAllGroupStandings() {
        log.info("GET /api/groups");
        List<GroupStandingDto> standings = groupStandingService.getAllGroupStandings();
        return ResponseEntity.ok(standings);
    }

    @GetMapping("/{groupName}")
    @Operation(summary = "Get group standings by group name", description = "Retrieve standings for a specific group calculated from match data")
    public ResponseEntity<List<GroupStandingDto>> getGroupStandingsByGroup(
            @Parameter(description = "Group name (e.g., GROUP_A, GROUP_B)")
            @PathVariable String groupName) {
        log.info("GET /api/groups/{}", groupName);
        List<GroupStandingDto> standings = groupStandingService.getGroupStandingsByGroup(groupName);
        return ResponseEntity.ok(standings);
    }
}
