package com.matchiq.matchiq.service;

import org.springframework.stereotype.Service;

import com.matchiq.matchiq.Dto.TeamComparisonResponse;
import com.matchiq.matchiq.Dto.TeamStatsResponse;
import com.matchiq.matchiq.Dto.TeamFormResponse;

@Service
public class TeamComparisonService {

    private final TeamStatsService teamStatsService;

    public TeamComparisonService(TeamStatsService teamStatsService) {
        this.teamStatsService = teamStatsService;
    }

    public TeamComparisonResponse compareTeams(
            Long firstTeamId,
            Long secondTeamId
    ) {

        TeamStatsResponse firstTeamStats =
                teamStatsService.calculateTeamStats(firstTeamId);

        TeamStatsResponse secondTeamStats =
                teamStatsService.calculateTeamStats(secondTeamId);

        TeamFormResponse firstTeamForm =
                teamStatsService.calculateRecentForm(firstTeamId);

        TeamFormResponse secondTeamForm =
                teamStatsService.calculateRecentForm(secondTeamId);

        return new TeamComparisonResponse(
                firstTeamStats,
                secondTeamStats,
                firstTeamForm,
                secondTeamForm
        );
    }
}