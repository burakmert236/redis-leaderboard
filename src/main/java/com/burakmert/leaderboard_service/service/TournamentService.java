package com.burakmert.leaderboard_service.service;

import com.burakmert.leaderboard_service.entity.Tournament;
import com.burakmert.leaderboard_service.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public Tournament GetOne(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElse(null);
    }

    public Tournament Create(String name) {
        Tournament tournament = Tournament.builder().name(name).build();
        return tournamentRepository.save(tournament);
    }

}
