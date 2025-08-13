package com.quickcache.QuickCache.services;

import com.quickcache.QuickCache.dto.LeaderboardResponse;
import com.quickcache.QuickCache.entities.Submission;
import com.quickcache.QuickCache.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 
 * LeaderboardService - Redis Sorted Set kullanarak Quiz skor tablosunu yönetir
 * 1. Quiz skorlarını Redis'te real-time güncellemek
 * 2. Top N kullanıcıları hızlı bir şekilde getirmek  
 * 3. Kullanıcının sıralamadaki yerini bulmak
 * 4. Database ile Redis'i hibrit olarak kullanmak
**/

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final RedisTemplate<String, String> leaderboardTemplate;
    private final SubmissionRepository submissionRepository;

    
    public void updateLeaderboard(Long quizId, Long userId, Integer score) {
        String leaderboardKey = "leaderboard:quiz:" + quizId;
        String userKey = "user:" + userId;
        
        leaderboardTemplate.opsForZSet().add(leaderboardKey, userKey, score.doubleValue());
        
        log.info("Updated leaderboard for quiz {}: user {} with score {}", quizId, userId, score);
    }

    
    public LeaderboardResponse getQuizLeaderboard(Long quizId, int limit) {
        String leaderboardKey = "leaderboard:quiz:" + quizId;
        
        Set<ZSetOperations.TypedTuple<String>> topScores = 
            leaderboardTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, limit - 1);
        
        List<LeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
        long rank = 1;
        
        if (topScores != null) {
            for (ZSetOperations.TypedTuple<String> entry : topScores) {
                String userKey = entry.getValue();
                Double score = entry.getScore();
                
                if (userKey != null && score != null) {
                    Long userId = extractUserIdFromKey(userKey);
                    if (userId != null) {
                        Submission submission = submissionRepository.findByUser_IdAndQuiz_Id(userId, quizId)
                                .orElse(null);
                        
                        if (submission != null) {
                            LeaderboardResponse.LeaderboardEntry leaderboardEntry = 
                                new LeaderboardResponse.LeaderboardEntry(
                                    rank, 
                                    submission.getUsername(), 
                                    score.intValue(), 
                                    submission.getSubmittedAt()
                                );
                            entries.add(leaderboardEntry);
                        }
                    }
                }
                rank++;
            }
        }
        
        return new LeaderboardResponse(quizId, "Quiz " + quizId, entries);
    }

    
    public Long getUserRank(Long quizId, Long userId) {
        String leaderboardKey = "leaderboard:quiz:" + quizId;
        String userKey = "user:" + userId;
        
        Long rank = leaderboardTemplate.opsForZSet().reverseRank(leaderboardKey, userKey);
        return rank != null ? rank + 1 : null; 
    }


    private Long extractUserIdFromKey(String userKey) {
        if (userKey != null && userKey.startsWith("user:")) {
            try {
                return Long.parseLong(userKey.substring(5));
            } catch (NumberFormatException e) {
                log.warn("Invalid user key format: {}", userKey);
            }
        }
        return null;
    }
}