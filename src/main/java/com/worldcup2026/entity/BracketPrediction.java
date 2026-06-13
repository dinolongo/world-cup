package com.worldcup2026.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "bracket_predictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BracketPrediction {

    @Id
    @Column(name = "bracket_id", length = 8)
    private String bracketId;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(name = "group_stage_predictions", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String groupStagePredictions;

    @Column(name = "knockout_predictions", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String knockoutPredictions;

    @Column(name = "total_score")
    private Integer totalScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
