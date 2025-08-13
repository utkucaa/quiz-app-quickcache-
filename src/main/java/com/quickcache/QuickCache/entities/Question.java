package com.quickcache.QuickCache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_quiz", columnList = "quiz_id"),
    @Index(name = "idx_question_type", columnList = "type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 1000, message = "Question text must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String text;

    @NotNull(message = "Question type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;

    @Column(name = "correct_option_index", nullable = false)
    private Integer correctOptionIndex;

    @Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer points = 1;

    @Size(max = 500, message = "Explanation must not exceed 500 characters")
    @Column(length = 500)
    private String explanation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Option> options = new ArrayList<>();


    public enum QuestionType {
        MULTIPLE_CHOICE("Multiple Choice"),
        TRUE_FALSE("True/False"),
        SINGLE_SELECT("Single Select");

        private final String displayName;

        QuestionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }


        public boolean requiresMultipleOptions() {
            return this == MULTIPLE_CHOICE || this == SINGLE_SELECT;
        }


        public int getMinimumOptions() {
            return switch (this) {
                case TRUE_FALSE -> 2;
                case MULTIPLE_CHOICE, SINGLE_SELECT -> 2;
                default -> 2;
            };
        }


        public int getMaximumOptions() {
            return switch (this) {
                case TRUE_FALSE -> 2;
                case MULTIPLE_CHOICE, SINGLE_SELECT -> 6;
                default -> 6;
            };
        }
    }


    public boolean isCorrectAnswer(Integer optionIndex) {
        return correctOptionIndex != null && correctOptionIndex.equals(optionIndex);
    }


    public Option getCorrectOption() {
        if (options != null && correctOptionIndex != null && 
            correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return options.get(correctOptionIndex);
        }
        return null;
    }


    public void addOption(Option option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
        option.setQuestion(this);
    }


    public void removeOption(Option option) {
        if (options != null) {
            options.remove(option);
            option.setQuestion(null);
        }
    }


    public boolean isValid() {
        if (options == null || type == null) {
            return false;
        }
        
        int optionCount = options.size();
        int minRequired = type.getMinimumOptions();
        int maxAllowed = type.getMaximumOptions();
        
        return optionCount >= minRequired && 
               optionCount <= maxAllowed &&
               correctOptionIndex != null &&
               correctOptionIndex >= 0 &&
               correctOptionIndex < optionCount;
    }

                
    public Long getQuizId() {
        return quiz != null ? quiz.getId() : null;
    }
}
