package com.quickcache.QuickCache.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

                
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long quizId;

    @NotEmpty
    private List<QuestionAnswer> answers;

    private Integer durationSeconds;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnswer {
        @NotNull
        private Long questionId;
        
        @NotNull
        private Integer selectedOptionIndex;
    }
}