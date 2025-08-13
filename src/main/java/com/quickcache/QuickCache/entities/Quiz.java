package com.quickcache.QuickCache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "quizzes", indexes = {
    @Index(name = "idx_quiz_category", columnList = "category"),
    @Index(name = "idx_quiz_active", columnList = "is_active"),
    @Index(name = "idx_quiz_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Quiz title is required")
    @Size(min = 3, max = 200, message = "Quiz title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String category;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private Integer duration; 

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @Column(name = "max_attempts", columnDefinition = "INT DEFAULT 1")
    private Integer maxAttempts = 1;

    @Column(name = "passing_score", columnDefinition = "INT DEFAULT 0")
    private Integer passingScore = 0;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Submission> submissions = new ArrayList<>();


    public void activate() {
        this.isActive = true;
    }


    public void deactivate() {
        this.isActive = false;
    }


    public int getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }


    public boolean isReadyForSubmission() {
        return isActive && getQuestionCount() > 0;
    }


    public int getMaximumScore() {
        return getQuestionCount(); 
    }


    public boolean isPassingScore(Integer score) {
        return score != null && score >= passingScore;
    }


    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
        question.setQuiz(this);
    }

        
    public void removeQuestion(Question question) {
        if (questions != null) {
            questions.remove(question);
            question.setQuiz(null);
        }
    }
}
