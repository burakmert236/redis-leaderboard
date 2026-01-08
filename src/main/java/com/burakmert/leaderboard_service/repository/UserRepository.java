package com.burakmert.leaderboard_service.repository;

import com.burakmert.leaderboard_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET u.level = u.level + :levels WHERE u.id = :id")
    int incrementLevel(@Param("id") Long id, @Param("levels") Integer levels);

}
