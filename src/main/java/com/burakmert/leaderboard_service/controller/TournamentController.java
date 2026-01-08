package com.burakmert.leaderboard_service.controller;

import com.burakmert.leaderboard_service.dto.tournament.CreateTournamentRequestDto;
import com.burakmert.leaderboard_service.entity.Tournament;
import com.burakmert.leaderboard_service.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/")
    public ResponseEntity<Tournament> Create(@RequestBody CreateTournamentRequestDto dto) {
        Tournament tournament = tournamentService.Create(dto.getName());
        return ResponseEntity.ok(tournament);
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<Tournament> GetOne(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.GetOne(tournamentId);
        return ResponseEntity.ok(tournament);
    }
}
