package com.quickcache.QuickCache.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcache.QuickCache.dto.QuizResponse;
import com.quickcache.QuickCache.entities.Quiz;
import com.quickcache.QuickCache.exception.QuizNotFoundException;
import com.quickcache.QuickCache.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
     * Quiz'i ID ile getirir - Redis Cache-Aside Pattern
     * 
     * İŞLEM AKIŞI:
     * 1. Redis'te cache key'i kontrol et
     * 2. Cache Hit → JSON'dan Quiz'e deserialize et, döndür
     * 3. Cache Miss → Database'e git
     * 4. Database'den quiz'i al (yoksa exception fırlat)
     * 5. Quiz'i JSON'a serialize et ve Redis'e kaydet
     * 6. Quiz'i döndür
**/
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public Quiz getQuizById(Long id) {
        String cacheKey = "quiz:cache:" + id;
        
        try {
            String cachedQuiz = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedQuiz != null) {
                log.debug("Quiz {} found in Redis cache", id);
                return objectMapper.readValue(cachedQuiz, Quiz.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize cached quiz {}", id);
        }

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found: " + id));

        try {
            String quizJson = objectMapper.writeValueAsString(quiz);
            redisTemplate.opsForValue().set(cacheKey, quizJson, 1, TimeUnit.HOURS);
            log.debug("Quiz {} cached in Redis", id);
        } catch (JsonProcessingException e) {
            log.error("Failed to cache quiz {}", id);
        }
        
        return quiz;
    }

    
    public QuizResponse getQuizForTaking(Long id) {
        Quiz quiz = getQuizById(id);
        if (!quiz.getIsActive()) {
            throw new IllegalStateException("Quiz is not active");
        }
        return new QuizResponse(quiz, true);
    }

    
    public Page<Quiz> getActiveQuizzes(Pageable pageable) {
        return quizRepository.findByIsActiveTrue(pageable);
    }

    
    public Page<Quiz> getQuizzesByCategory(String category, Pageable pageable) {
        return quizRepository.findByCategory(category, pageable);
    }

            
    public void invalidateQuizCache(Long quizId) {
        String cacheKey = "quiz:cache:" + quizId;
        redisTemplate.delete(cacheKey);
        log.debug("Invalidated cache for quiz: {}", quizId);
    }
}