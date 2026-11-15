package com.matchiq.matchiq.Dto;

public record HeadToHeadResponse(
        Long firstTeamId,
        String firstTeamName,
        Long secondTeamId,
        String secondTeamName,
        int matchesPlayed,
        int firstTeamWins,
        int secondTeamWins,
        int draws,
        int firstTeamGoals,
        int secondTeamGoals
) {}