package com.matchiq.matchiq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchiq.matchiq.Dto.api.ApiFixturesResponse;
import com.matchiq.matchiq.client.FootballApiClient;

@RestController
@RequestMapping("/api/test")

public class FootballApiTestController {
     private final FootballApiClient footballApiClient;

    public FootballApiTestController(FootballApiClient footballApiClient) {
        this.footballApiClient = footballApiClient;
    }

    @GetMapping("/fixtures")
    public ApiFixturesResponse testFixtures() {
        return footballApiClient.getFixtures(39, 2024);
    }
}
