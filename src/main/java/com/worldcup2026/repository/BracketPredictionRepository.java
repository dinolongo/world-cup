package com.worldcup2026.repository;

import com.worldcup2026.entity.BracketPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BracketPredictionRepository extends JpaRepository<BracketPrediction, String> {

    Optional<BracketPrediction> findByDisplayName(String displayName);

    boolean existsByDisplayName(String displayName);
}
