package com.quickcache.QuickCache.repository;

import com.quickcache.QuickCache.entities.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByIsActiveTrue();
    Page<Quiz> findByIsActiveTrue(Pageable pageable);
    List<Quiz> findByCategory(String category);
    Page<Quiz> findByCategory(String category, Pageable pageable);
    Optional<Quiz> findByIdAndIsActiveTrue(Long id);
}