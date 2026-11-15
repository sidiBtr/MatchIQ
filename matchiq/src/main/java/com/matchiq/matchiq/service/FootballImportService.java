package com.matchiq.matchiq.service;
import org.springframework.stereotype.Service;

import com.matchiq.matchiq.Dto.api.ApiMatch;
import com.matchiq.matchiq.Dto.api.ApiTeam;
import com.matchiq.matchiq.entity.Match;
import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.repository.MatchRepository;
import com.matchiq.matchiq.repository.TeamRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.matchiq.matchiq.client.FootballApiClient;
import com.matchiq.matchiq.Dto.api.ApiFixturesResponse;
@Service
public class FootballImportService {
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final FootballApiClient footballApiClient;
    public FootballImportService(TeamRepository teamRepository, 
        MatchRepository matchRepository,
        FootballApiClient footballApiClient

    ) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.footballApiClient = footballApiClient;
    }

    public Team saveTeam(ApiTeam apiTeam) {

        return teamRepository.findByExternalApiId(apiTeam.id())
                .orElseGet(() -> {
                    Team team = new Team();

                    team.setExternalApiId(apiTeam.id());
                    team.setName(apiTeam.name());

                    return teamRepository.save(team);
                });
    }
    public Match saveMatch(ApiMatch apiMatch) {

    // Step 1: Find or create the two teams
    Team homeTeam = saveTeam(apiMatch.teams().home());
    Team awayTeam = saveTeam(apiMatch.teams().away());

    // Step 2: Find the match or create a new one
    Match match = matchRepository
            .findByExternalApiId(apiMatch.fixture().id())
            .orElseGet(Match::new);

    // Step 3: Connect the teams
    match.setHomeTeam(homeTeam);
    match.setAwayTeam(awayTeam);

    // Step 4: Convert the API date to UTC LocalDateTime
    LocalDateTime matchDate = apiMatch.fixture()
            .date()
            .withOffsetSameInstant(ZoneOffset.UTC)
            .toLocalDateTime();

    match.setMatchDate(matchDate);

    // Step 5: Set the scores and match status
    match.setHomeScore(apiMatch.goals().home());
    match.setAwayScore(apiMatch.goals().away());
    match.setStatus(apiMatch.fixture().status().shortCode());

    // Step 6: Store the external API ID
    match.setExternalApiId(apiMatch.fixture().id());

    // Step 7: Save the match
        return matchRepository.save(match);
    }
    public int importFixtures(int leagueId, int season) {

    // Fetch fixtures from API-Football
    ApiFixturesResponse apiResponse =
            footballApiClient.getFixtures(leagueId, season);

    // Count successfully saved matches
    int importedCount = 0;

    // Loop through every fixture
    for (ApiMatch apiMatch : apiResponse.response()) {

        saveMatch(apiMatch);

        importedCount++;
    }

    return importedCount;
}
}
