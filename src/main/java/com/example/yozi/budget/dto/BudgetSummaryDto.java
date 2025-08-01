package com.example.yozi.budget.dto;

public record BudgetSummaryDto(
        int total,
        int spent,
        int remaining,
        int exceeded,
        int prevTotal,
        int prevSpent,
        int prevRemaining,
        int prevExceeded
) {}
