package com.example.yozi.budget.dto;

import com.example.yozi.transaction.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FinancialScheduleDto {
    private LocalDate date;
    private String partnerName;
    private int amount;
    private TransactionType transactionType;
}