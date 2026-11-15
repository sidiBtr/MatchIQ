package com.matchiq.matchiq.Dto;

import java.util.List;

public record TeamFormResponse(
        Long teamId,
        String teamName,
        List<String> recentForm
) {}