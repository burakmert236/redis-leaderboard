package com.burakmert.leaderboard_service.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisLeaderboardPage {
    private List<LeaderboardEntry> entries;
    private Long lastUpdated;
    private Boolean isStale;
}
