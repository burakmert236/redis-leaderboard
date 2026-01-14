package com.burakmert.leaderboard_service.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {

    public LeaderboardEntry(Long userId, String username, Long score) {
        this.userId = userId;
        this.username = username;
        this.score = score.intValue();
    }

    private Long userId;
    private String username;
    private int score;
}
