package com.quickcache.QuickCache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Simple Submission entity for quiz results
 */
@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @NotNull
    @Column(nullable = false)
    private Integer score;

    @Column(name = "max_possible_score", nullable = false)
    private Integer maxPossibleScore;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "is_passed", nullable = false)
    private Boolean isPassed = false;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    // Store user answers as JSON
    @ElementCollection
    @CollectionTable(name = "submission_answers", 
                    joinColumns = @JoinColumn(name = "submission_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "selected_option_index")
    private Map<Long, Integer> answers;

    /**
     * Get user ID
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    /**
     * Get quiz ID
     */
    public Long getQuizId() {
        return quiz != null ? quiz.getId() : null;
    }

    /**
     * Get username
     */
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    /**
     * Calculate percentage score
     */
    public double getPercentageScore() {
        if (maxPossibleScore == null || maxPossibleScore == 0) {
            return 0.0;
        }
        return (score.doubleValue() / maxPossibleScore.doubleValue()) * 100.0;
    }
}