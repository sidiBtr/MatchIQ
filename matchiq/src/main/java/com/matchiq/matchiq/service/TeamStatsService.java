package com.matchiq.matchiq.service;
import com.matchiq.matchiq.Dto.TeamFormResponse;
import com.matchiq.matchiq.Dto.TeamStatsResponse;
import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.entity.Match;
import com.matchiq.matchiq.repository.TeamRepository;
import com.matchiq.matchiq.repository.MatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamStatsService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public TeamStatsService(
            TeamRepository teamRepository,
            MatchRepository matchRepository
    ) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public TeamStatsResponse calculateTeamStats(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Team not found with ID: " + teamId
                        )
                );

        List<Match> matches =
                matchRepository.findByHomeTeam_IdOrAwayTeam_Id(
                        teamId,
                        teamId
                );
                int matchesPlayed = 0;
                int wins = 0;
                int draws = 0;
                int losses = 0;
                int goalsScored = 0;
                int goalsConceded = 0;
                for (Match match : matches) {

                // Only count completed matches
                if (!"FT".equals(match.getStatus())) {
                    continue;
                }

                // Ignore matches without final scores
                if (match.getHomeScore() == null ||
                    match.getAwayScore() == null) {
                    continue;
                }

                matchesPlayed++;

                boolean isHomeTeam =
                        match.getHomeTeam().getId().equals(teamId);

                int teamGoals;
                int opponentGoals;

                if (isHomeTeam) {
                    teamGoals = match.getHomeScore();
                    opponentGoals = match.getAwayScore();
                } else {
                    teamGoals = match.getAwayScore();
                    opponentGoals = match.getHomeScore();
                }

                goalsScored += teamGoals;
                goalsConceded += opponentGoals;

                if (teamGoals > opponentGoals) {
                    wins++;
                } else if (teamGoals == opponentGoals) {
                    draws++;
                } else {
                    losses++;
                }
            }
            int goalDifference = goalsScored - goalsConceded;

            double winPercentage = matchesPlayed == 0
            ? 0.0
            : (wins * 100.0) / matchesPlayed;

            return new TeamStatsResponse(
            team.getId(),
            team.getName(),
            matchesPlayed,
            wins,
            draws,
            losses,
            goalsScored,
            goalsConceded,
            goalDifference,
            winPercentage
    );

    }
    @Transactional(readOnly = true)
    public TeamFormResponse calculateRecentForm(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Team not found with ID: " + teamId
                )
                );

        List<Match> matches =
                matchRepository
                        .findByHomeTeam_IdOrAwayTeam_IdOrderByMatchDateDesc(
                                teamId, teamId
                        );

        List<String> recentForm = new ArrayList<>();

        for (Match match : matches) {

            // Stop after collecting five completed matches
            if (recentForm.size() == 5) {
                break;
            }

            // Ignore unfinished matches
            if (!"FT".equals(match.getStatus())) {
                continue;
            }

            // Ignore matches without scores
            if (match.getHomeScore() == null ||
                    match.getAwayScore() == null) {
                continue;
            }

            boolean isHomeTeam =
                    match.getHomeTeam().getId().equals(teamId);

            int teamGoals = isHomeTeam
                    ? match.getHomeScore()
                    : match.getAwayScore();

            int opponentGoals = isHomeTeam
                    ? match.getAwayScore()
                    : match.getHomeScore();

            if (teamGoals > opponentGoals) {
                recentForm.add("W");
            } else if (teamGoals == opponentGoals) {
                recentForm.add("D");
            } else {
                recentForm.add("L");
            }
        }

        return new TeamFormResponse(
                team.getId(),
                team.getName(),
                recentForm
        );
    }
}