package com.example.yozi.budget.repository;

import com.example.yozi.budget.entity.Budget;
import com.example.yozi.category.entity.Category;
import com.example.yozi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // 해당 월 예산 총합 계산, null 반환 가능성 대비 Long 사용
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Budget b WHERE b.user = :user AND b.budgetMonth = :month")
    Long sumAmountByUserAndMonth(@Param("user") User user, @Param("month") LocalDate month);

    // 특정 사용자, 카테고리, 월 예산 단건 조회
    Optional<Budget> findByUserAndCategoryAndBudgetMonth(User user, Category category, LocalDate budgetMonth);

    // 특정 사용자와 월에 해당하는 모든 예산 조회
    List<Budget> findByUserAndBudgetMonth(User user, LocalDate budgetMonth);

}
