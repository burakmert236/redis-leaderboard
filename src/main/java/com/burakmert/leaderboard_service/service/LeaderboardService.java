package com.burakmert.leaderboard_service.service;

import com.burakmert.leaderboard_service.entity.Leaderboard;
import com.burakmert.leaderboard_service.repository.LeaderboardRepository;
import com.burakmert.leaderboard_service.repository.TournamentRepository;
import com.burakmert.leaderboard_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final LeaderboardRepository leaderboardRepository;

    public Leaderboard GetOne(Long leaderboardId) {
        return leaderboardRepository.findById(leaderboardId).orElse(null);
    }

    public Leaderboard Create(Long tournamentId, Long userId) {
        Leaderboard leaderboard = Leaderboard.builder()
                .tournament(tournamentRepository.findById(tournamentId).orElse(null))
                .user(userRepository.findById(userId).orElse(null))
                .build();
        return leaderboardRepository.save(leaderboard);
    }

}
