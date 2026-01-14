package com.burakmert.leaderboard_service.repository;

import com.burakmert.leaderboard_service.dto.leaderboard.LeaderboardEntry;
import com.burakmert.leaderboard_service.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    @Modifying
    @Query("UPDATE Leaderboard l SET l.score = l.score + :levels " +
            "WHERE l.tournament.id = :t_id AND l.user.id = :u_id")
    int incrementScore(@Param("t_id") Long tournamentId,
                       @Param("u_id") Long userId,
                       @Param("levels") Integer levels);

    Optional<Leaderboard> findFirstByUserId(Long userId);

    @Query("""
        SELECT new com.burakmert.leaderboard_service.dto.leaderboard.LeaderboardEntry(
            l.user.id,
            l.user.username,
            SUM(l.score)
        )
        FROM Leaderboard l
        GROUP BY l.user.id, l.user.username
        ORDER BY SUM(l.score) DESC, l.user.id ASC
        LIMIT 2500
        """)
    List<LeaderboardEntry> getTop2500();
}
