package com.example.yozi.transaction.dto;

import com.example.yozi.transaction.entity.enums.PaymentMethod;
import com.example.yozi.transaction.entity.enums.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter @Setter
public class TransactionDto {
    private Long userId;
    private TransactionType type;
    private Integer categoryId;
    private PaymentMethod paymentMethod;
    private String vendor;
    private Integer amount;
    private String memo;
    private LocalDate transactionDate;
}
