package com.worldcup2026.repository;

import com.worldcup2026.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByExternalApiId(Integer externalApiId);

    List<Match> findByStatus(Match.MatchStatus status);

    List<Match> findByUtcDateAfter(LocalDateTime date);

    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Match> findByHomeTeamId(Long homeTeamId);

    List<Match> findByAwayTeamId(Long awayTeamId);

    boolean existsByExternalApiId(Integer externalApiId);
}
