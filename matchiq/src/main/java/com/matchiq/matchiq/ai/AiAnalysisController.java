
package com.matchiq.matchiq.ai;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matchiq.matchiq.Dto.TeamComparisonResponse;
import com.matchiq.matchiq.service.TeamComparisonService;

@RestController
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;
    private final TeamComparisonService teamComparisonService;

    public AiAnalysisController(
            AiAnalysisService aiAnalysisService,
            TeamComparisonService teamComparisonService) {

        this.aiAnalysisService = aiAnalysisService;
        this.teamComparisonService = teamComparisonService;
    }

    @GetMapping("/api/ai/analyze")
    public ResponseEntity<Map<String, String>> analyzeTeams(
            @RequestParam Long firstTeamId,
            @RequestParam Long secondTeamId) {

        // Step 1: Get real statistics from our database
        TeamComparisonResponse comparison =
                teamComparisonService.compareTeams(
                        firstTeamId,
                        secondTeamId
                );

        // Step 2: Build the prompt using those statistics
        String prompt = """
                You are a professional soccer analyst.

                Analyze the following two teams using ONLY the
                provided statistics from the 2024-25 Premier League season.

                Explain:
                1. Which team performed better overall and why.
                2. Their attacking and defensive strengths.
                3. What their recent form suggests.
                4. A short conclusion comparing both teams.

                Do not invent statistics, injuries, players,
                or information not included in the data.
                Do not predict future match outcomes.

                Keep the analysis clear, balanced, and concise.

                Team comparison data:
                """ + comparison;

        // Step 3: Send the prompt to OpenAI
        String analysis =
                aiAnalysisService.generateAnalysis(prompt);

        // Step 4: Return the generated analysis as JSON
        return ResponseEntity.ok(
                Map.of("analysis", analysis)
        );
    }
}
