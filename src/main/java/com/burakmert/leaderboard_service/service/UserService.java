package com.burakmert.leaderboard_service.service;

import com.burakmert.leaderboard_service.entity.Leaderboard;
import com.burakmert.leaderboard_service.entity.User;
import com.burakmert.leaderboard_service.repository.LeaderboardRepository;
import com.burakmert.leaderboard_service.repository.UserRepository;
import com.burakmert.leaderboard_service.repository.redis.RedisLeaderboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final RedisLeaderboardRepository redisLeaderboardRepository;

    public User GetOne(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User Create(String username) {
        User user = User.builder().username(username).build();
        return userRepository.save(user);
    }

    @Transactional
    public void LevelUp(Long userId, Integer levelIncrease) throws Exception {
        int updatedCount = userRepository.incrementLevel(userId, levelIncrease);
        if (updatedCount == 0) {
            throw new Exception("User can not be leveled up");
        }

        Leaderboard leaderboard = leaderboardRepository.findFirstByUserId(userId)
                .orElse(null);

        if (leaderboard != null) {
            int scoreUpdatedCount = leaderboardRepository.incrementScore(leaderboard.getTournament().getId(), userId, levelIncrease);
            if (scoreUpdatedCount == 0) {
                throw new Exception("Score can not be updated");
            }

            CompletableFuture.runAsync(() -> {
                try {
                    redisLeaderboardRepository.updateGlobalScore(userId, levelIncrease, false);
                } catch (Exception e) {
                    log.error("Redis update failed, will be corrected on next refresh", e);
                }
            });
        }
    }

}
