package com.quickcache.QuickCache.services;

import com.quickcache.QuickCache.dto.SubmitRequest;
import com.quickcache.QuickCache.dto.SubmitResponse;
import com.quickcache.QuickCache.entities.Question;
import com.quickcache.QuickCache.entities.Quiz;
import com.quickcache.QuickCache.entities.Submission;
import com.quickcache.QuickCache.entities.User;
import com.quickcache.QuickCache.exception.SubmissionAlreadyExistsException;
import com.quickcache.QuickCache.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
     * Quiz cevaplarını işler - Full business logic
     * 
     * İŞLEM AKIŞI:
     * 1. Redis'te duplicate check
     * 2. User ve Quiz bilgilerini getir
     * 3. Cevapları değerlendir ve skor hesapla
     * 4. Submission entity'si oluştur
     * 5. Database'e kaydet
     * 6. User skorunu güncelle
     * 7. Leaderboard'u güncelle
     * 8. Redis'e duplicate mark ekle
     * 9. Response döndür 
**/

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final QuizService quizService;
    private final LeaderboardService leaderboardService;
    private final StringRedisTemplate stringRedisTemplate;


    public SubmitResponse submitQuiz(SubmitRequest request) {
        Long userId = request.getUserId();
        Long quizId = request.getQuizId();

        String lockKey = "submission:quiz:" + quizId;
        Boolean alreadySubmitted = stringRedisTemplate.opsForSet().isMember(lockKey, userId.toString());
        
        if (Boolean.TRUE.equals(alreadySubmitted)) {
            throw new SubmissionAlreadyExistsException("User already submitted this quiz");
        }

        User user = userService.findById(userId);
        Quiz quiz = quizService.getQuizById(quizId);

        int score = 0;
        int correctAnswers = 0;
        int totalQuestions = quiz.getQuestions().size();
        
        Map<Long, Integer> answersMap = new HashMap<>();
        for (SubmitRequest.QuestionAnswer answer : request.getAnswers()) {
            answersMap.put(answer.getQuestionId(), answer.getSelectedOptionIndex());
        }

        for (Question question : quiz.getQuestions()) {
            Integer userAnswer = answersMap.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectOptionIndex())) {
                score += question.getPoints();
                correctAnswers++;
            }
        }

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setQuiz(quiz);
        submission.setScore(score);
        submission.setMaxPossibleScore(quiz.getMaximumScore());
        submission.setCorrectAnswers(correctAnswers);
        submission.setTotalQuestions(totalQuestions);
        submission.setTimeTakenSeconds(request.getDurationSeconds());
        submission.setAnswers(answersMap);
        submission.setIsPassed(score >= quiz.getPassingScore());

        submissionRepository.save(submission);

        userService.updateUserScore(userId, score);

        leaderboardService.updateLeaderboard(quizId, userId, score);

        stringRedisTemplate.opsForSet().add(lockKey, userId.toString());
        stringRedisTemplate.expire(lockKey, 24, TimeUnit.HOURS);

        Long rank = leaderboardService.getUserRank(quizId, userId);

        return SubmitResponse.create(score, totalQuestions, correctAnswers, 
                                   submission.getIsPassed(), rank);
    }

            
    public boolean canUserSubmitQuiz(Long userId, Long quizId) {
        String lockKey = "submission:quiz:" + quizId;
        Boolean submitted = stringRedisTemplate.opsForSet().isMember(lockKey, userId.toString());
        return !Boolean.TRUE.equals(submitted);
    }
}