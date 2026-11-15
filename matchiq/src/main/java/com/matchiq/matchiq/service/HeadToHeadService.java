package com.matchiq.matchiq.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.matchiq.matchiq.Dto.HeadToHeadResponse;
import com.matchiq.matchiq.entity.Match;
import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.repository.MatchRepository;
import com.matchiq.matchiq.repository.TeamRepository;

@Service
public class HeadToHeadService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public HeadToHeadService(
            MatchRepository matchRepository,
            TeamRepository teamRepository
    ) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public HeadToHeadResponse calculateHeadToHead(
            Long firstTeamId,
            Long secondTeamId
    ) {

        if (firstTeamId.equals(secondTeamId)) {
            throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Cannot compare a team with itself"
            );
        }

        Team firstTeam = teamRepository.findById(firstTeamId)
                .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "First team not found with ID: " + firstTeamId
                )
                );

        Team secondTeam = teamRepository.findById(secondTeamId)
               .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Second team not found with ID: " + secondTeamId
                )
                );

        List<Match> matches = matchRepository
                .findByHomeTeam_IdAndAwayTeam_IdOrHomeTeam_IdAndAwayTeam_IdOrderByMatchDateDesc(
                        firstTeamId, secondTeamId,
                        secondTeamId, firstTeamId
                );

        int matchesPlayed = 0;
        int firstTeamWins = 0;
        int secondTeamWins = 0;
        int draws = 0;
        int firstTeamGoals = 0;
        int secondTeamGoals = 0;

        for (Match match : matches) {

            if (!"FT".equals(match.getStatus())) {
                continue;
            }

            if (match.getHomeScore() == null ||
                    match.getAwayScore() == null) {
                continue;
            }

            matchesPlayed++;

            boolean firstTeamIsHome =
                    match.getHomeTeam().getId().equals(firstTeamId);

            int goalsFirst = firstTeamIsHome
                    ? match.getHomeScore()
                    : match.getAwayScore();

            int goalsSecond = firstTeamIsHome
                    ? match.getAwayScore()
                    : match.getHomeScore();

            firstTeamGoals += goalsFirst;
            secondTeamGoals += goalsSecond;

            if (goalsFirst > goalsSecond) {
                firstTeamWins++;
            } else if (goalsFirst < goalsSecond) {
                secondTeamWins++;
            } else {
                draws++;
            }
        }

        return new HeadToHeadResponse(
                firstTeam.getId(),
                firstTeam.getName(),
                secondTeam.getId(),
                secondTeam.getName(),
                matchesPlayed,
                firstTeamWins,
                secondTeamWins,
                draws,
                firstTeamGoals,
                secondTeamGoals
        );
    }
}