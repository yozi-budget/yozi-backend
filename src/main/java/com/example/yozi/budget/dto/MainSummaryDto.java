package com.example.yozi.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MainSummaryDto {
    private int totalBudget;
    private int totalIncome;
    private int totalExpense;
    private List<FinancialScheduleDto> futureSchedules;
}