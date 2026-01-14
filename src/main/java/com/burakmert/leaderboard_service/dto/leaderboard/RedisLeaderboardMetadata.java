package com.burakmert.leaderboard_service.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisLeaderboardMetadata {
    private Long lastUpdated;
    private Integer version;
    private Long totalPlayers;
}
