package com.worldcup2026.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupStandingDto {

    private String groupName;
    private String teamId;
    private String teamName;

    private int played;
    private int wins;
    private int draws;
    private int losses;

    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;

    private int points;
}
