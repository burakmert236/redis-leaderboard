package com.burakmert.leaderboard_service.repository.redis;

import com.burakmert.leaderboard_service.dto.leaderboard.LeaderboardEntry;
import com.burakmert.leaderboard_service.dto.leaderboard.RedisLeaderboardPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisLeaderboardRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyHelper keyHelper;

    private static final int GLOBAL_LEADERBOARD_SIZE = 2500; // Keep buffer beyond 2000
    private static final long GLOBAL_FRESHNESS_SECONDS = 30;
    private static final long TOURNAMENT_FRESHNESS_SECONDS = 5;
    private static final long USER_RANK_CACHE_TTL_SECONDS = 15;
    private static final int REFRESH_LOCK_TTL_SECONDS = 30;

    // ==================== Global Leaderboard Operations ====================

    /*
     * Update or add a user score to the global leaderboard
     */
    public void updateGlobalScore(Long userId, int score, boolean setAbsolute) {
        try {
            String leaderboardKey = keyHelper.globalLeaderboard();

            if (setAbsolute) {
                redisTemplate.opsForZSet().add(leaderboardKey, userId.toString(), score);
            } else {
                redisTemplate.opsForZSet().incrementScore(leaderboardKey, userId.toString(), score);
            }

            redisTemplate.opsForZSet().removeRange(leaderboardKey, 0, -(GLOBAL_LEADERBOARD_SIZE + 1));

            updateMetadata(keyHelper.globalMetadata());
        } catch (Exception e) {
            log.error("Failed to update global score for user {}", userId, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /*
     * Get global leaderboard
     */
    public RedisLeaderboardPage getGlobalLeaderboard(int limit) {
        try {
            String leaderboardKey = keyHelper.globalLeaderboard();

            Set<ZSetOperations.TypedTuple<Object>> entries =
                    redisTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, limit - 1);

            Long lastUpdated = Long.parseLong(
                    Objects.requireNonNull(
                            redisTemplate.opsForHash().get(
                                    keyHelper.globalMetadata(),
                                    "last_updated")).toString());

            log.info("Redis entries and last updated, {}, {}", entries != null ? entries.size() : 0, lastUpdated);

            return RedisLeaderboardPage.builder()
                    .entries(convertLeaderboardEntries(entries))
                    .lastUpdated(lastUpdated)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get global leaderboard", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /*
     * Rebuild entire leaderboard
     */
    public void rebuildGlobalLeaderboard(List<LeaderboardEntry> entries) {
        redisTemplate.delete(keyHelper.globalLeaderboard());

        entries.forEach(entry -> {
           redisTemplate.opsForZSet().add(keyHelper.globalLeaderboard(), entry.getUserId().toString(), entry.getScore());
        });

        updateMetadata(keyHelper.globalMetadata());
    }

    // ==================== Helper Methods ====================

    /*
     * Update metadata for a leaderboard
     */
    private void updateMetadata(String metadataKey) {
        redisTemplate.opsForHash().put(metadataKey, "last_updated", String.valueOf(System.currentTimeMillis()));
        redisTemplate.opsForHash().increment(metadataKey, "version", 1);
    }

    /*
     * Try to acquire refresh lock
     */
    public boolean tryAcquireRefreshLock(String leaderboardKey) {
        String lockKey = keyHelper.refreshLock(leaderboardKey);
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS));
    }

    /*
     * Release refresh lock
     */
    public void releaseRefreshLock(String leaderboardKey) {
        String lockKey = keyHelper.refreshLock(leaderboardKey);
        redisTemplate.opsForValue().getAndDelete(lockKey);
    }

    /*
     * Convert redis zset entries to RedisLeaderboardPage
     */
    private List<LeaderboardEntry> convertLeaderboardEntries(Set<ZSetOperations.TypedTuple<Object>> entries) {
        List<LeaderboardEntry> page = new ArrayList<>(Collections.emptyList());

        if (entries == null || entries.isEmpty()) {
            return page;
        }

        for (ZSetOperations.TypedTuple<Object> entry : entries) {
            if (entry.getValue() != null && entry.getScore() != null) {
                page.add(LeaderboardEntry.builder()
                        .userId(Long.parseLong(entry.getValue().toString()))
                        .score((int)Double.parseDouble(entry.getScore().toString()))
                        .build());
            }
        }

        return page;
    }
}
