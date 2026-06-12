package com.worldcup2026.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches", indexes = {
    @Index(name = "idx_match_external_api_id", columnList = "external_api_id"),
    @Index(name = "idx_match_home_team", columnList = "home_team_id"),
    @Index(name = "idx_match_away_team", columnList = "away_team_id"),
    @Index(name = "idx_match_status", columnList = "status"),
    @Index(name = "idx_match_utc_date", columnList = "utc_date"),
    @Index(name = "idx_match_stadium", columnList = "stadium_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_api_id", unique = true, nullable = false)
    private Integer externalApiId;

    @Column(name = "home_team_id", nullable = false)
    private Long homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private Long awayTeamId;

    @Column(name = "utc_date", nullable = false)
    private LocalDateTime utcDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MatchStatus status;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "stage", length = 50)
    private String stage;

    @Column(name = "group_name", length = 20)
    private String group;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", length = 20)
    private MatchDuration duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum MatchStatus {
        SCHEDULED,
        LIVE,
        IN_PLAY,
        PAUSED,
        FINISHED,
        POSTPONED,
        SUSPENDED,
        CANCELLED,
        TIMED
    }

    public enum MatchDuration {
        REGULAR,
        EXTRA_TIME,
        PENALTY_SHOOTOUT
    }
}
