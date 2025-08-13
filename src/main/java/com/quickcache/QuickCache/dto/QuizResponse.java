package com.quickcache.QuickCache.dto;

import com.quickcache.QuickCache.entities.Option;
import com.quickcache.QuickCache.entities.Question;
import com.quickcache.QuickCache.entities.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

    
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;
    private String title;
    private String category;
    private Integer duration;
    private Integer questionCount;
    private List<QuestionDto> questions;

    public QuizResponse(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.category = quiz.getCategory();
        this.duration = quiz.getDuration();
        this.questionCount = quiz.getQuestionCount();
    }

    public QuizResponse(Quiz quiz, boolean includeQuestions) {
        this(quiz);
        if (includeQuestions && quiz.getQuestions() != null) {
            this.questions = quiz.getQuestions().stream()
                    .map(QuestionDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private Long id;
        private String text;
        private String type;
        private List<OptionDto> options;

        public QuestionDto(Question question) {
            this.id = question.getId();
            this.text = question.getText();
            this.type = question.getType().name();
            if (question.getOptions() != null) {
                this.options = question.getOptions().stream()
                        .map(OptionDto::new)
                        .collect(Collectors.toList());
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDto {
        private Long id;
        private String text;

        public OptionDto(Option option) {
            this.id = option.getId();
            this.text = option.getText();
        }
    }
}