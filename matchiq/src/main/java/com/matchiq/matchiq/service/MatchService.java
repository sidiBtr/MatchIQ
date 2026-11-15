
package com.matchiq.matchiq.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchiq.matchiq.entity.Match;
import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.repository.MatchRepository;
import com.matchiq.matchiq.repository.TeamRepository;
import com.matchiq.matchiq.Dto.MatchResponse;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public MatchService(
            MatchRepository matchRepository,
            TeamRepository teamRepository) {

        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Match createMatch(
            Long homeTeamId,
            Long awayTeamId,
            LocalDateTime matchDate,
            Integer homeScore,
            Integer awayScore,
            String status,
            Integer externalApiId) {

            if (homeTeamId.equals(awayTeamId)) {
                throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Home and away teams must be different"
    );
}

        Team homeTeam = teamRepository.findById(homeTeamId)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Home team not found: " + homeTeamId
        ));

        Team awayTeam = teamRepository.findById(awayTeamId)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Away team not found: " + awayTeamId
        ));

        Match match = new Match(
                homeTeam,
                awayTeam,
                matchDate,
                homeScore,
                awayScore,
                status,
                externalApiId
        );

        return matchRepository.save(match);
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    public List<Match> getMatchesByStatus(String status) {
        return matchRepository.findByStatus(status);
    }

    public List<Match> getMatchesByTeam(Long teamId) {
        return matchRepository.findByHomeTeam_IdOrAwayTeam_Id(
                teamId, teamId
        );
    }

    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
    private MatchResponse toResponse(Match match) {
    return new MatchResponse(
            match.getId(),
            match.getHomeTeam().getId(),
            match.getHomeTeam().getName(),
            match.getAwayTeam().getId(),
            match.getAwayTeam().getName(),
            match.getMatchDate(),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getStatus(),
            match.getExternalApiId()
    );
}

@Transactional(readOnly = true)
public List<MatchResponse> getAllMatchResponses() {
    return matchRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public Optional<MatchResponse> getMatchResponseById(Long id) {
    return matchRepository.findById(id)
            .map(this::toResponse);
}

@Transactional(readOnly = true)
public List<MatchResponse> getMatchResponsesByStatus(String status) {
    return matchRepository.findByStatus(status)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public List<MatchResponse> getMatchResponsesByTeam(Long teamId) {
    return matchRepository.findByHomeTeam_IdOrAwayTeam_Id(teamId, teamId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

@Transactional
public MatchResponse createMatchResponse(
        Long homeTeamId,
        Long awayTeamId,
        LocalDateTime matchDate,
        Integer homeScore,
        Integer awayScore,
        String status,
        Integer externalApiId) {

    Match saved = createMatch(
            homeTeamId,
            awayTeamId,
            matchDate,
            homeScore,
            awayScore,
            status,
            externalApiId
    );

    return toResponse(saved);
}
}

