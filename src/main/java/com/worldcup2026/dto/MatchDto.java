package com.worldcup2026.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.worldcup2026.entity.Match;
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
public class MatchDto {

    private Long id;

    @NotNull(message = "External API ID is required")
    private Integer externalApiId;

    @NotNull(message = "Home team is required")
    private TeamDto homeTeam;

    @NotNull(message = "Away team is required")
    private TeamDto awayTeam;

    @NotNull(message = "UTC date is required")
    private LocalDateTime utcDate;

    @NotNull(message = "Match status is required")
    private Match.MatchStatus status;

    private Integer homeScore;

    private Integer awayScore;

    private String stage;

    private String group;

    private LocalDateTime lastUpdated;
}
