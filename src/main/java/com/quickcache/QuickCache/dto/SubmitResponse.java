package com.quickcache.QuickCache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResponse {

    private Integer score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Boolean passed;
    private Long rank;
    private Double percentage;

    public static SubmitResponse create(Integer score, Integer total, Integer correct, 
                                      Boolean passed, Long rank) {
        SubmitResponse response = new SubmitResponse();
        response.setScore(score);
        response.setTotalQuestions(total);
        response.setCorrectAnswers(correct);
        response.setPassed(passed);
        response.setRank(rank);
        response.setPercentage(total > 0 ? (score * 100.0 / total) : 0.0);
        return response;
    }
}