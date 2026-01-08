package com.burakmert.leaderboard_service.dto.leaderboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardResponse {

    private Long id;

    private Long tournamentId;

    private Long userId;

    private int score;

}
