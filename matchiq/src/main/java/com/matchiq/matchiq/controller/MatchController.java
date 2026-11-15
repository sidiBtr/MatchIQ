
package com.matchiq.matchiq.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.matchiq.matchiq.Dto.MatchRequest;
import com.matchiq.matchiq.Dto.MatchResponse;
import com.matchiq.matchiq.service.MatchService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // GET /api/matches
    @GetMapping
    public List<MatchResponse> getAllMatches() {
        return matchService.getAllMatchResponses();
    }

    // GET /api/matches/1
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(
            @PathVariable Long id) {

        return matchService.getMatchResponseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/matches
    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(
            @Valid @RequestBody MatchRequest request) {

        MatchResponse saved = matchService.createMatchResponse(
                request.homeTeamId(),
                request.awayTeamId(),
                request.matchDate(),
                request.homeScore(),
                request.awayScore(),
                request.status(),
                request.externalApiId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/matches/status/FINISHED
    @GetMapping("/status/{status}")
    public List<MatchResponse> getMatchesByStatus(
            @PathVariable String status) {

        return matchService.getMatchResponsesByStatus(status);
    }

    // GET /api/matches/team/1
    @GetMapping("/team/{teamId}")
    public List<MatchResponse> getMatchesByTeam(
            @PathVariable Long teamId) {

        return matchService.getMatchResponsesByTeam(teamId);
    }

    // DELETE /api/matches/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {

        if (matchService.getMatchById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}
