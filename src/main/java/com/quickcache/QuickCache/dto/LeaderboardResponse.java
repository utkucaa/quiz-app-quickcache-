package com.quickcache.QuickCache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {

    private Long quizId;
    private String quizTitle;
    private List<LeaderboardEntry> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private Long rank;
        private String username;
        private Integer score;
        private LocalDateTime submittedAt;
    }
}