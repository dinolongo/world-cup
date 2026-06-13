package com.worldcup2026.service;

import com.worldcup2026.entity.BracketPrediction;
import com.worldcup2026.repository.BracketPredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BracketPredictionService {

    private final BracketPredictionRepository bracketPredictionRepository;

    /**
     * Generate a unique 8-character bracket ID
     */
    public String generateBracketId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder bracketId = new StringBuilder();
        
        // Generate random 8-character ID
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * characters.length());
            bracketId.append(characters.charAt(index));
        }
        
        // Ensure uniqueness
        if (bracketPredictionRepository.existsById(bracketId.toString())) {
            log.info("Generated bracket ID already exists, regenerating...");
            return generateBracketId(); // Recursively try again
        }
        
        return bracketId.toString();
    }

    /**
     * Check if display name is available
     */
    public boolean isDisplayNameAvailable(String displayName) {
        log.info("Checking availability for display name: {}", displayName);
        return !bracketPredictionRepository.existsByDisplayName(displayName);
    }

    /**
     * Save bracket prediction
     */
    @Transactional
    public BracketPrediction saveBracketPrediction(String displayName, String groupStagePredictions, String knockoutPredictions) {
        log.info("Saving bracket prediction for display name: {}", displayName);
        
        String bracketId = generateBracketId();
        
        BracketPrediction prediction = BracketPrediction.builder()
                .bracketId(bracketId)
                .displayName(displayName)
                .groupStagePredictions(groupStagePredictions)
                .knockoutPredictions(knockoutPredictions)
                .totalScore(0) // Will be calculated later
                .build();
        
        return bracketPredictionRepository.save(prediction);
    }

    /**
     * Get bracket prediction by bracket ID
     */
    public Optional<BracketPrediction> getBracketPrediction(String bracketId) {
        log.info("Fetching bracket prediction for ID: {}", bracketId);
        return bracketPredictionRepository.findById(bracketId);
    }

    /**
     * Get all bracket predictions
     */
    public List<BracketPrediction> getAllBracketPredictions() {
        log.info("Fetching all bracket predictions");
        return bracketPredictionRepository.findAll();
    }
}
