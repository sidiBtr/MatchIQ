package com.matchiq.matchiq.Dto;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        Long homeTeamId,
        String homeTeamName,
        Long awayTeamId,
        String awayTeamName,
        LocalDateTime matchDate,
        Integer homeScore,
        Integer awayScore,
        String status,
        Integer externalApiId
) {
    
}
