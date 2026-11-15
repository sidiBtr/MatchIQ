package com.matchiq.matchiq.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.matchiq.matchiq.Dto.api.ApiFixturesResponse;

@Component
public class FootballApiClient {
    private final RestClient restClient;

    public FootballApiClient(
            @Value("${football.api.base-url}") String baseUrl,
            @Value("${football.api.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-apisports-key", apiKey)
                .build();
    }

    public ApiFixturesResponse getFixtures(int leagueId, int season) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .body(ApiFixturesResponse.class);
    }
}
