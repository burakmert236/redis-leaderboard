package com.burakmert.leaderboard_service.repository;

import com.burakmert.leaderboard_service.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> { }
