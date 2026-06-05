package com.worldcup2026.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupStandingDto {

    private Long id;

    @NotBlank(message = "Group name is required")
    private String groupName;

    @NotNull(message = "Team is required")
    private TeamDto team;

    private Integer position;

    private Integer playedGames;

    private Integer wins;

    private Integer draws;

    private Integer losses;

    private Integer goalsFor;

    private Integer goalsAgainst;

    private Integer goalDifference;

    private Integer points;

    private LocalDateTime lastUpdated;
}
