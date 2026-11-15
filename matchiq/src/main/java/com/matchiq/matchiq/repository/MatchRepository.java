
package com.matchiq.matchiq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchiq.matchiq.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByHomeTeamId(Long teamId);

    List<Match> findByAwayTeamId(Long teamId);

    List<Match> findByStatus(String status);

    List<Match> findByHomeTeam_IdOrAwayTeam_Id(
            Long homeTeamId,
            Long awayTeamId
    );
    Optional<Match> findByExternalApiId(Integer externalApiId);
        List<Match> findByHomeTeam_IdOrAwayTeam_IdOrderByMatchDateDesc(
            Long homeTeamId,
            Long awayTeamId
        );
        List<Match> findByHomeTeam_IdAndAwayTeam_IdOrHomeTeam_IdAndAwayTeam_IdOrderByMatchDateDesc(
            Long firstHomeTeamId,
            Long firstAwayTeamId,
            Long secondHomeTeamId,
            Long secondAwayTeamId
        );
}
