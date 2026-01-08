package com.burakmert.leaderboard_service.dto.leaderboard;

import lombok.Data;

@Data
public class CreateLeaderboardRequestDto {

    private Long tournamentId;

    private Long userId;

}
