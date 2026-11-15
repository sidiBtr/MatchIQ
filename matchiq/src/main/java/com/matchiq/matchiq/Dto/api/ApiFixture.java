package com.matchiq.matchiq.Dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFixture(
        Integer id,
        OffsetDateTime date,
        ApiStatus status
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiStatus(
            @JsonProperty("short") String shortCode,
            @JsonProperty("long") String longDescription
    ) {}
}
