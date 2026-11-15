package com.matchiq.matchiq.Dto;

public record TeamStatsResponse(
        Long teamId,
        String teamName,
        int matchesPlayed,
        int wins,
        int draws,
        int losses,
        int goalsScored,
        int goalsConceded,
        int goalDifference,
        double winPercentage
) {
}