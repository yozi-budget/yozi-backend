package com.example.yozi.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAnalysisDto {

    private int currentMonthTotal;
    private double currentMonthAverage;
    private int previousMonthTotal;
    private int twoMonthsAgoTotal;

    private List<TransactionDetail> transactions;

    // 소비 습관 점수 관련 필드 추가
    private int habitScore;
    private int habitScoreChange;
    private List<String> habitFeedbackMessages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionDetail {
        private LocalDate date;
        private String vendor;
        private int amount;
    }
}
