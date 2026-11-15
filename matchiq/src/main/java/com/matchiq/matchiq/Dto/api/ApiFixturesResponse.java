package com.matchiq.matchiq.Dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFixturesResponse(
        Integer results,
        List<ApiMatch> response
) {}