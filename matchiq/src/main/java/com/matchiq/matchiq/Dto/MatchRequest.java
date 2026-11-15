package com.matchiq.matchiq.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MatchRequest(
       @NotNull(message = "Home team ID is required")
        Long homeTeamId,

    @NotNull(message = "Away team ID is required")
        Long awayTeamId,

    @NotNull(message = "Match date is required")
        LocalDateTime matchDate,

    @Min(value = 0, message = "Home score cannot be negative")
        Integer homeScore,

    @Min(value = 0, message = "Away score cannot be negative")
        Integer awayScore,

    @NotBlank(message = "Match status is required")
        String status,

        Integer externalApiId

) {
    
}
