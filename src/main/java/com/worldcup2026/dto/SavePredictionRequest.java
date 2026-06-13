package com.worldcup2026.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavePredictionRequest {
    private String displayName;
    private String groupStagePredictions;
    private String knockoutPredictions;
}
