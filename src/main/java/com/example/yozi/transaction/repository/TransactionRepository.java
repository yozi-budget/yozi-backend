package com.example.yozi.transaction.repository;

import com.example.yozi.transaction.entity.Transaction;
import com.example.yozi.transaction.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);


    @EntityGraph(attributePaths = {"user"})
    List<Transaction> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.type = :type")
    Long sumAmountByUserAndDateRangeAndType(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("type") TransactionType type);

    List<Transaction> findByUserIdAndCategoryId(Long userId, Integer categoryId);

}
