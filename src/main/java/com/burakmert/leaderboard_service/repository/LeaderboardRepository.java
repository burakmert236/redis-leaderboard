package com.burakmert.leaderboard_service.repository;

import com.burakmert.leaderboard_service.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    @Modifying
    @Query("UPDATE Leaderboard l SET l.score = l.score + :levels " +
            "WHERE l.tournament.id = :t_id AND l.user.id = :u_id")
    int incrementScore(@Param("t_id") Long tournamentId,
                       @Param("u_id") Long userId,
                       @Param("levels") Integer levels);

    Optional<Leaderboard> findFirstByUserId(Long userId);
}
