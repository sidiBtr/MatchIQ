package com.matchiq.matchiq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.matchiq.matchiq.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
      Optional<Team> findByExternalApiId(Integer externalApiId);
}
