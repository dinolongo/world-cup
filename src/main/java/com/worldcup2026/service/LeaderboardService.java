package com.worldcup2026.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldcup2026.dto.LeaderboardEntryDTO;
import com.worldcup2026.entity.BracketPrediction;
import com.worldcup2026.repository.BracketPredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

    private final BracketPredictionRepository bracketPredictionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Get all leaderboard entries
     */
    public List<LeaderboardEntryDTO> getLeaderboard() {
        log.info("Fetching all leaderboard entries");
        
        Sort sort = Sort.by(
                Sort.Order.desc("totalScore"),
                Sort.Order.asc("createdAt")
        );
        
        List<BracketPrediction> predictions = bracketPredictionRepository.findAll(sort);
        
        return predictions.stream()
                .map(this::mapToLeaderboardEntryDTO)
                .toList();
    }

    /**
     * Get total count of bracket predictions
     */
    public long getTotalBracketCount() {
        return bracketPredictionRepository.count();
    }

    /**
     * Map BracketPrediction entity to LeaderboardEntryDTO
     */
    private LeaderboardEntryDTO mapToLeaderboardEntryDTO(BracketPrediction prediction) {
        LeaderboardEntryDTO.LeaderboardEntryDTOBuilder builder = LeaderboardEntryDTO.builder()
                .bracketId(prediction.getBracketId())
                .displayName(prediction.getDisplayName())
                .totalScore(prediction.getTotalScore())
                .createdAt(prediction.getCreatedAt());

        // Parse knockout predictions to derive champion, runner-up, and third place
        try {
            Map<String, String> knockoutPredictions = objectMapper.readValue(
                    prediction.getKnockoutPredictions(),
                    new TypeReference<Map<String, String>>() {}
            );

            // Match 104 is the Final - winner is the champion
            String championId = knockoutPredictions.get("104");
            builder.predictedChampionId(championId);

            // Match 103 is the Third Place match - winner is third place
            String thirdPlaceId = knockoutPredictions.get("103");
            builder.predictedThirdPlaceId(thirdPlaceId);

            // Runner-up is the loser of match 104
            // Since we only store winners, we need match data to determine this
            // For now, we'll set it to null and can enhance later
            builder.predictedRunnerUpId(null);

            // Parse group stage predictions to calculate group stage score
            // This will be populated when scoring runs
            builder.groupStageScore(null);
            builder.knockoutScore(null);

        } catch (Exception e) {
            log.error("Failed to parse knockout predictions for bracket: {}", prediction.getBracketId(), e);
        }

        return builder.build();
    }
}
