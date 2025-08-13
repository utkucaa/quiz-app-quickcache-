package com.quickcache.QuickCache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Simple Option entity for quiz questions
 */
@Entity
@Table(name = "options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String text;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor with text and order
     */
    public Option(String text, Integer optionOrder) {
        this.text = text;
        this.optionOrder = optionOrder;
    }

    /**
     * Get question ID
     */
    public Long getQuestionId() {
        return question != null ? question.getId() : null;
    }
}