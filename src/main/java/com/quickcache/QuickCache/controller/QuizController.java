package com.quickcache.QuickCache.controller;

import com.quickcache.QuickCache.dto.QuizResponse;
import com.quickcache.QuickCache.dto.SubmitRequest;
import com.quickcache.QuickCache.dto.SubmitResponse;
import com.quickcache.QuickCache.entities.Quiz;
import com.quickcache.QuickCache.services.QuizService;
import com.quickcache.QuickCache.services.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final SubmissionService submissionService;

    
    @GetMapping
    public ResponseEntity<Page<QuizResponse>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizzes = quizService.getActiveQuizzes(pageable);
        Page<QuizResponse> response = quizzes.map(QuizResponse::new);
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        QuizResponse response = quizService.getQuizForTaking(id);
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmitResponse> submitQuiz(
            @PathVariable Long id,
            @Valid @RequestBody SubmitRequest request) {
        
        request.setQuizId(id);
        SubmitResponse response = submissionService.submitQuiz(request);
        
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/can-submit")
    public ResponseEntity<Boolean> canUserSubmitQuiz(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        boolean canSubmit = submissionService.canUserSubmitQuiz(userId, id);
        return ResponseEntity.ok(canSubmit);
    }
}