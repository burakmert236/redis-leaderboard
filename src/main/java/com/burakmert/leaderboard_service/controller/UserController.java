package com.burakmert.leaderboard_service.controller;

import com.burakmert.leaderboard_service.dto.user.CreateUserRequestDto;
import com.burakmert.leaderboard_service.dto.user.UserLevelUpRequestDto;
import com.burakmert.leaderboard_service.entity.User;
import com.burakmert.leaderboard_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<User> Create(@RequestBody CreateUserRequestDto dto) {
        User user = userService.Create(dto.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> GetOne(@PathVariable Long userId) {
        User user = userService.GetOne(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> UserLevelUp(
            @PathVariable Long userId,
            @RequestBody UserLevelUpRequestDto dto) throws Exception {
        userService.LevelUp(userId, dto.getLevelIncrease());
        return ResponseEntity.ok("success");
    }
}
