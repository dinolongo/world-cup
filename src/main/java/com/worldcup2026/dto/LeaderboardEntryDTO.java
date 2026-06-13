package com.worldcup2026.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class LeaderboardEntryDTO {

    private String bracketId;
    private String displayName;
    private String predictedChampionId;
    private String predictedRunnerUpId;
    private String predictedThirdPlaceId;
    private Integer totalScore;
    private Integer groupStageScore;
    private Integer knockoutScore;
    private LocalDateTime createdAt;
}
