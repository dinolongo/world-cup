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
public class TeamDto {

    private Long id;

    @NotNull(message = "External API ID is required")
    private Integer externalApiId;

    @NotBlank(message = "Team name is required")
    private String name;

    private String shortName;

    private String tla;

    private String crestUrl;

    private LocalDateTime lastUpdated;
}
