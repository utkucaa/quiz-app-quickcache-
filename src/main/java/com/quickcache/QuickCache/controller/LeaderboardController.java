package com.quickcache.QuickCache.controller;

import com.quickcache.QuickCache.dto.LeaderboardResponse;
import com.quickcache.QuickCache.services.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    
    @GetMapping("/{quizId}")
    public ResponseEntity<LeaderboardResponse> getQuizLeaderboard(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "10") int limit) {
        
        LeaderboardResponse response = leaderboardService.getQuizLeaderboard(quizId, limit);
        return ResponseEntity.ok(response);
    }

                            
    @GetMapping("/{quizId}/user/{userId}/rank")
    public ResponseEntity<Long> getUserRank(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        
        Long rank = leaderboardService.getUserRank(quizId, userId);
        return ResponseEntity.ok(rank);
    }
}