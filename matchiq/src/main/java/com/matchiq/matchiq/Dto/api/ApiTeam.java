package com.matchiq.matchiq.Dto.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiTeam(
        Integer id,
        String name
) {
    
}
