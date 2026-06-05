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
@Table(name = "group_standings", indexes = {
    @Index(name = "idx_standing_group", columnList = "group_name"),
    @Index(name = "idx_standing_team", columnList = "team_id"),
    @Index(name = "idx_standing_group_team", columnList = "group_name, team_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupStanding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 10)
    private String groupName;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "position")
    private Integer position;

    @Column(name = "played_games")
    private Integer playedGames;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "draws")
    private Integer draws;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "goals_for")
    private Integer goalsFor;

    @Column(name = "goals_against")
    private Integer goalsAgainst;

    @Column(name = "goal_difference")
    private Integer goalDifference;

    @Column(name = "points")
    private Integer points;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
