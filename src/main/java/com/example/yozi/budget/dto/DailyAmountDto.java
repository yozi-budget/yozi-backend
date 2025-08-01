package com.example.yozi.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyAmountDto {
    private LocalDate date;
    private int income;
    private int expense;
}