package com.quickcache.QuickCache.repository;

import com.quickcache.QuickCache.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    Optional<Submission> findByUser_IdAndQuiz_Id(Long userId, Long quizId);
    boolean existsByUser_IdAndQuiz_Id(Long userId, Long quizId);
    List<Submission> findByUser_IdOrderBySubmittedAtDesc(Long userId);
    List<Submission> findByQuiz_IdOrderByScoreDesc(Long quizId);
    Page<Submission> findByQuiz_IdOrderByScoreDesc(Long quizId, Pageable pageable);
}