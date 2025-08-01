package com.example.yozi.budget.dto;

import com.example.yozi.category.entity.enums.CategoryType;

public record BudgetResponseDto(
        CategoryType categoryType,
        int amount
) {}
