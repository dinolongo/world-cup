package com.worldcup2026.controller;

import com.worldcup2026.dto.*;
import com.worldcup2026.entity.BracketPrediction;
import com.worldcup2026.service.BracketPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Predictions", description = "World Cup bracket predictions endpoints")
public class PredictionController {

    private final BracketPredictionService bracketPredictionService;

    @PostMapping("/check-name")
    @Operation(summary = "Check if display name is available")
    public ResponseEntity<CheckNameResponse> checkDisplayName(
            @Parameter(description = "Display name to check")
            @RequestBody CheckNameRequest request) {
        log.info("POST /api/predictions/check-name - displayName: {}", request.getDisplayName());
        
        boolean available = bracketPredictionService.isDisplayNameAvailable(request.getDisplayName());
        
        CheckNameResponse response = CheckNameResponse.builder()
                .available(available)
                .message(available ? "Display name is available" : "Display name already taken")
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @Operation(summary = "Save bracket prediction")
    public ResponseEntity<SavePredictionResponse> savePrediction(
            @Parameter(description = "Prediction data to save")
            @RequestBody SavePredictionRequest request) {
        log.info("POST /api/predictions/save - displayName: {}", request.getDisplayName());
        
        // Validate display name is available
        if (!bracketPredictionService.isDisplayNameAvailable(request.getDisplayName())) {
            return ResponseEntity.badRequest().body(
                    SavePredictionResponse.builder()
                            .message("Display name already taken")
                            .build()
            );
        }
        
        BracketPrediction prediction = bracketPredictionService.saveBracketPrediction(
                request.getDisplayName(),
                request.getGroupStagePredictions(),
                request.getKnockoutPredictions()
        );
        
        SavePredictionResponse response = SavePredictionResponse.builder()
                .bracketId(prediction.getBracketId())
                .displayName(prediction.getDisplayName())
                .message("Bracket saved successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bracketId}")
    @Operation(summary = "Get bracket prediction by ID")
    public ResponseEntity<BracketPrediction> getPrediction(
            @Parameter(description = "Bracket ID")
            @PathVariable String bracketId) {
        log.info("GET /api/predictions/{}", bracketId);
        
        return bracketPredictionService.getBracketPrediction(bracketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all bracket predictions")
    public ResponseEntity<List<BracketPrediction>> getAllPredictions() {
        log.info("GET /api/predictions");
        
        List<BracketPrediction> predictions = bracketPredictionService.getAllBracketPredictions();
        return ResponseEntity.ok(predictions);
    }
}
