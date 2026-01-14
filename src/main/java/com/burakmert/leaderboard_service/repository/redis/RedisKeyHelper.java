package com.burakmert.leaderboard_service.repository.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyHelper {

    private static final String GLOBAL_LEADERBOARD = "global:leaderboard";
    private static final String GLOBAL_METADATA = "global:leaderboard:meta";
    private static final String TOURNAMENT_GROUP_LEADERBOARD = "tournament:%d:group:%d:leaderboard";
    private static final String TOURNAMENT_GROUP_METADATA = "tournament:%d:group:%d:meta";
    private static final String USER_TOURNAMENT_GROUP = "user:%d:tournament:%d:group";
    private static final String REFRESH_LOCK = "refresh_lock:%s";

    public String globalLeaderboard() {
        return GLOBAL_LEADERBOARD;
    }

    public String globalMetadata() {
        return GLOBAL_METADATA;
    }

    public String tournamentGroupLeaderboard(Long tournamentId, Long groupId) {
        return String.format(TOURNAMENT_GROUP_LEADERBOARD, tournamentId, groupId);
    }

    public String tournamentGroupMetadata(Long tournamentId, Long groupId) {
        return String.format(TOURNAMENT_GROUP_METADATA, tournamentId, groupId);
    }

    public String userTournamentGroup(Long userId, Long tournamentId) {
        return String.format(USER_TOURNAMENT_GROUP, userId, tournamentId);
    }

    public String refreshLock(String leaderboardKey) {
        return String.format(REFRESH_LOCK, leaderboardKey);
    }

}
