package com.matchiq.matchiq.Dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiGoals(
    Integer home,
    Integer away
) {
    
}
