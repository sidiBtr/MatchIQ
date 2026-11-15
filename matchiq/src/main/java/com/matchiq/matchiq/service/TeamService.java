package com.matchiq.matchiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.matchiq.matchiq.entity.Team;
import com.matchiq.matchiq.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // Save a new team
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    // Retrieve all teams
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    // Retrieve a team by its ID
        public Team getTeamById(Long id) {
            return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Team not found with ID: " + id
                ));
        }

    // Delete a team
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Team not found with ID: " + id
        );
    }

    teamRepository.deleteById(id);
}
}