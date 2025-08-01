package com.example.yozi.transaction.dto;

import com.example.yozi.transaction.entity.enums.PaymentMethod;
import com.example.yozi.transaction.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private String userNickname;
    private TransactionType type;
    private Integer categoryId;
    private String categoryDisplayName;  // 카테고리 이름 표시용
    private PaymentMethod paymentMethod;
    private String vendor;
    private Integer amount;
    private String memo;
    private LocalDate transactionDate;
}
