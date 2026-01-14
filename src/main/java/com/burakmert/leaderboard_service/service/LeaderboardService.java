package com.burakmert.leaderboard_service.service;

import com.burakmert.leaderboard_service.dto.leaderboard.LeaderboardEntry;
import com.burakmert.leaderboard_service.dto.leaderboard.RedisLeaderboardPage;
import com.burakmert.leaderboard_service.entity.Leaderboard;
import com.burakmert.leaderboard_service.repository.LeaderboardRepository;
import com.burakmert.leaderboard_service.repository.TournamentRepository;
import com.burakmert.leaderboard_service.repository.UserRepository;
import com.burakmert.leaderboard_service.repository.redis.RedisKeyHelper;
import com.burakmert.leaderboard_service.repository.redis.RedisLeaderboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final RedisLeaderboardRepository redisLeaderboardRepository;

    private final RedisKeyHelper keyHelper;

    private final static int GLOBAL_LEADERBOARD_SIZE = 2000;
    private final static int GLOBAL_METADATA_STALE_SECONDS = 30;

    public Leaderboard GetOne(Long leaderboardId) {
        return leaderboardRepository.findById(leaderboardId).orElse(null);
    }

    public Leaderboard Create(Long tournamentId, Long userId) {
        Leaderboard leaderboard = Leaderboard.builder()
                .tournament(tournamentRepository.findById(tournamentId).orElse(null))
                .user(userRepository.findById(userId).orElse(null))
                .build();

        leaderboard = leaderboardRepository.save(leaderboard);

        CompletableFuture.runAsync(() -> {
            try {
                redisLeaderboardRepository.updateGlobalScore(userId, 0, true);
            } catch (Exception e) {
                log.error("Redis update failed, will be corrected on next refresh", e);
            }
        });

        return leaderboard;
    }

    public RedisLeaderboardPage GetGlobalLeaderboard() {
        log.info("Getting global leaderboard from redis");
        RedisLeaderboardPage page = redisLeaderboardRepository.getGlobalLeaderboard(GLOBAL_LEADERBOARD_SIZE);
        page.setIsStale(isStale(page.getLastUpdated()));

        if (page.getIsStale()) {
            log.info("Redis global leaderboard is stale");
            CompletableFuture.runAsync(this::refreshGlobalLeaderboard);
        }

        return page;
    }

    // ==================== Helper Methods ====================

    private void refreshGlobalLeaderboard() {
        if (redisLeaderboardRepository.tryAcquireRefreshLock(keyHelper.globalLeaderboard())) {
            try {
                log.info("Redis global leaderboard refresh lock acquired");

                List<LeaderboardEntry> entries = leaderboardRepository.getTop2500();
                log.info("Top 2500 entries fetched from postgres");

                redisLeaderboardRepository.rebuildGlobalLeaderboard(entries);
                log.info("Redis global leaderboard rebuilt");
            } finally {
                redisLeaderboardRepository.releaseRefreshLock(keyHelper.globalLeaderboard());
            }
        }
    }

    // ==================== Helper Static Methods ====================

    private static Boolean isStale(Long timeStamp) {
        if (timeStamp == null) {
            return false;
        }
        return System.currentTimeMillis() - timeStamp > (GLOBAL_METADATA_STALE_SECONDS * 1000);
    }
}
