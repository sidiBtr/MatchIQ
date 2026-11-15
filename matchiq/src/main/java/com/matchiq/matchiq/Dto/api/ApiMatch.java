package com.matchiq.matchiq.Dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiMatch(
        ApiFixture fixture,
        ApiTeams teams,
        ApiGoals goals
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiTeams(
            ApiTeam home,
            ApiTeam away
    ) {}
}