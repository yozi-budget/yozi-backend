package com.example.yozi.transaction.entity;

import com.example.yozi.transaction.entity.enums.PaymentMethod;
import com.example.yozi.transaction.entity.enums.TransactionType;
import com.example.yozi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Integer categoryId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String vendor;

    private Integer amount;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDate transactionDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}