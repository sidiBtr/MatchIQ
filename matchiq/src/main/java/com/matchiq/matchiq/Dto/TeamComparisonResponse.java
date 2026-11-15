package com.matchiq.matchiq.Dto;

public record TeamComparisonResponse(
        TeamStatsResponse firstTeam,
        TeamStatsResponse secondTeam,
        TeamFormResponse firstTeamForm,
        TeamFormResponse secondTeamForm
) {
    
}