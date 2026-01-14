package com.burakmert.leaderboard_service.controller;

import com.burakmert.leaderboard_service.dto.leaderboard.CreateLeaderboardRequestDto;
import com.burakmert.leaderboard_service.dto.leaderboard.LeaderboardResponse;
import com.burakmert.leaderboard_service.dto.leaderboard.RedisLeaderboardPage;
import com.burakmert.leaderboard_service.dto.tournament.CreateTournamentRequestDto;
import com.burakmert.leaderboard_service.entity.Leaderboard;
import com.burakmert.leaderboard_service.entity.Tournament;
import com.burakmert.leaderboard_service.service.LeaderboardService;
import com.burakmert.leaderboard_service.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/leaderboards")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @PostMapping("/")
    public ResponseEntity<Leaderboard> EnterTournament(@RequestBody CreateLeaderboardRequestDto dto) {
        Leaderboard leaderboard = leaderboardService.Create(dto.getTournamentId(), dto.getUserId());
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{leaderboardId}")
    public ResponseEntity<LeaderboardResponse> GetOne(@PathVariable Long leaderboardId) {
        Leaderboard leaderboard = leaderboardService.GetOne(leaderboardId);
        LeaderboardResponse response = LeaderboardResponse.builder()
                .id(leaderboard.getId())
                .tournamentId(leaderboard.getTournament().getId())
                .userId(leaderboard.getUser().getId())
                .score(leaderboard.getScore())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/global")
    public ResponseEntity<RedisLeaderboardPage> GetGlobalLeaderboard() {
        RedisLeaderboardPage leaderboard = leaderboardService.GetGlobalLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
