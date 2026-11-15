package com.matchiq.matchiq.controller;
import com.matchiq.matchiq.Dto.TeamFormResponse;
import com.matchiq.matchiq.service.TeamComparisonService;
import com.matchiq.matchiq.Dto.TeamComparisonResponse;
import com.matchiq.matchiq.service.HeadToHeadService;
import com.matchiq.matchiq.Dto.HeadToHeadResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.matchiq.matchiq.service.TeamStatsService;
import com.matchiq.matchiq.Dto.TeamStatsResponse;

import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.service.TeamService;

@RestController
@RequestMapping("/api/teams")

public class TeamController {
    private final TeamService teamService;
    private final TeamStatsService teamStatsService;
    private final TeamComparisonService teamComparisonService;
    private final HeadToHeadService headToHeadService;

    public TeamController(TeamService teamService,
                         TeamStatsService teamStatsService,
                         TeamComparisonService teamComparisonService,
                         HeadToHeadService headToHeadService
    ) {
        this.teamService = teamService;
        this.teamStatsService = teamStatsService;
        this.teamComparisonService = teamComparisonService;
        this.headToHeadService = headToHeadService;
    }

    // GET /api/teams
    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    // GET /api/teams/1
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {

        Team team = teamService.getTeamById(id);

        return ResponseEntity.ok(team);
    }

    // POST /api/teams
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team savedTeam = teamService.saveTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
    }

    // DELETE /api/teams/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {

        teamService.deleteTeam(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/stats")
    public TeamStatsResponse getTeamStats(
            @PathVariable Long teamId
    ) {
        return teamStatsService.calculateTeamStats(teamId);
    }
    @GetMapping("/{teamId}/form")
    public TeamFormResponse getTeamForm(
            @PathVariable Long teamId
    ) {
        return teamStatsService.calculateRecentForm(teamId);
    }

    @GetMapping("/compare")
    public TeamComparisonResponse compareTeams(
            @RequestParam Long firstTeamId,
            @RequestParam Long secondTeamId
    ) {
        return teamComparisonService.compareTeams(
                firstTeamId,
                secondTeamId
        );
    }
    @GetMapping("/head-to-head")
    public HeadToHeadResponse getHeadToHead(
            @RequestParam Long firstTeamId,
            @RequestParam Long secondTeamId
    ) {
        return headToHeadService.calculateHeadToHead(
                firstTeamId,
                secondTeamId
        );
    }
}
