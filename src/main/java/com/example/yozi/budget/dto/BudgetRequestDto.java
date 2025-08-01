package com.example.yozi.budget.dto;

import com.example.yozi.category.entity.enums.CategoryType;

import java.time.LocalDate;

public record BudgetRequestDto(
        CategoryType categoryType,
        int amount,
        LocalDate budgetMonth // ← 여기!
) {}
