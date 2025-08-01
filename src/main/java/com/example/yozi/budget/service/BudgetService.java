package com.example.yozi.budget.service;

import com.example.yozi.budget.dto.*;
import com.example.yozi.budget.entity.Budget;
import com.example.yozi.budget.repository.BudgetRepository;
import com.example.yozi.category.entity.Category;
import com.example.yozi.category.repository.CategoryRepository;
import com.example.yozi.transaction.entity.Transaction;
import com.example.yozi.transaction.entity.enums.TransactionType;
import com.example.yozi.transaction.repository.TransactionRepository;
import com.example.yozi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public void setBudget(User user, LocalDate date, List<BudgetRequestDto> budgets) {
        LocalDate budgetMonth = date.withDayOfMonth(1);

        for (BudgetRequestDto dto : budgets) {
            Category category = categoryRepository.findByType(dto.categoryType())
                    .orElseThrow(() -> new RuntimeException("Invalid category: " + dto.categoryType()));

            Budget budget = budgetRepository.findByUserAndCategoryAndBudgetMonth(user, category, budgetMonth)
                    .orElse(new Budget());

            budget.setUser(user);
            budget.setCategory(category);
            budget.setAmount(dto.amount());
            budget.setBudgetMonth(budgetMonth);

            budgetRepository.save(budget);
        }
    }

    public List<BudgetResponseDto> getBudget(User user, LocalDate date) {
        LocalDate budgetMonth = date.withDayOfMonth(1);
        List<Budget> budgets = budgetRepository.findByUserAndBudgetMonth(user, budgetMonth);

        return budgets.stream()
                .map(b -> new BudgetResponseDto(b.getCategory().getType(), b.getAmount()))
                .collect(Collectors.toList());
    }

    public int calculateTotalBudget(User user, LocalDate date) {
        LocalDate budgetMonth = date.withDayOfMonth(1);
        return budgetRepository.findByUserAndBudgetMonth(user, budgetMonth).stream()
                .mapToInt(Budget::getAmount)
                .sum();
    }

    public int calculateIncomeAmount(User user, LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());
        Long sum = transactionRepository.sumAmountByUserAndDateRangeAndType(user.getId(), start, end, TransactionType.INCOME);
        return sum != null ? sum.intValue() : 0;
    }

    public int calculateExpenseAmount(User user, LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());
        Long sum = transactionRepository.sumAmountByUserAndDateRangeAndType(user.getId(), start, end, TransactionType.EXPENSE);
        return sum != null ? sum.intValue() : 0;
    }

    public int calculateRemainingBudget(User user, LocalDate date) {
        int total = calculateTotalBudget(user, date);
        int spent = calculateExpenseAmount(user, date);
        int remaining = total - spent;
        return remaining < 0 ? 0 : remaining;
    }

    public int calculateExceededBudget(User user, LocalDate date) {
        int total = calculateTotalBudget(user, date);
        int spent = calculateExpenseAmount(user, date);
        int exceeded = spent - total;
        return exceeded > 0 ? exceeded : 0;
    }

    public BudgetSummaryDto getBudgetSummary(User user, LocalDate date) {
        int total = calculateTotalBudget(user, date);
        int Spent = calculateExpenseAmount(user, date);
        int remaining = total - Spent;
        int exceeded = remaining < 0 ? -remaining : 0;
        int prevTotal = calculateTotalBudget(user, date);
        int prevSpent = calculateExpenseAmount(user, date);
        int prevRemaining = prevTotal - prevSpent;
        int prevExceeded = remaining < 0 ? -remaining : 0;

        return new BudgetSummaryDto(total, Spent, remaining < 0 ? 0 : remaining, exceeded,
                prevTotal, prevSpent, prevRemaining < 0 ? 0 : prevRemaining, prevExceeded);
    }

    public MainSummaryDto getMainSummary(User user) {
        LocalDate now = LocalDate.now();

        int totalBudget = calculateTotalBudget(user, now);
        int totalIncome = calculateIncomeAmount(user, now);
        int totalExpense = calculateExpenseAmount(user, now);
        List<FinancialScheduleDto> futureSchedules = getFutureFinancialSchedules(user, now);

        return new MainSummaryDto(totalBudget, totalIncome, totalExpense, futureSchedules);
    }

    // 날짜별 수입·지출 (월 단위)
    public List<DailyAmountDto> getDailyIncomeExpense(User user, LocalDate month) {
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), start, end);

        Map<LocalDate, DailyAmountDto> dailyMap = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionDate,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            int incomeSum = list.stream()
                                    .filter(t -> t.getType() == TransactionType.INCOME)
                                    .mapToInt(Transaction::getAmount)
                                    .sum();
                            int expenseSum = list.stream()
                                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                                    .mapToInt(Transaction::getAmount)
                                    .sum();
                            return new DailyAmountDto(list.get(0).getTransactionDate(), incomeSum, expenseSum);
                        })));

        return dailyMap.values().stream()
                .filter(d -> d.getIncome() > 0 || d.getExpense() > 0)  // 0원 날짜 제외
                .collect(Collectors.toList());
    }

    public List<FinancialScheduleDto> getFutureFinancialSchedules(User user, LocalDate month) {
        LocalDate today = LocalDate.now();
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());

        // 오늘 이후의 해당 월 내 일정만 필터
        LocalDate scheduleStart = today.isAfter(start) ? today : start;

        List<Transaction> futureTransactions = transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(),
                scheduleStart, end);

        return futureTransactions.stream()
                .map(t -> new FinancialScheduleDto(t.getTransactionDate(), t.getVendor(), t.getAmount(), t.getType()))
                .collect(Collectors.toList());
    }


    public MonthlyAnalysisDto getMonthlyAnalysis(User user) {
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDate twoMonthsAgoStart = currentMonthStart.minusMonths(2);

        int currentTotal = calculateExpenseAmount(user, currentMonthStart);
        int previousTotal = calculateExpenseAmount(user, previousMonthStart);
        int twoMonthsAgoTotal = calculateExpenseAmount(user, twoMonthsAgoStart);

        int daysPassed = now.getDayOfMonth();
        double average = daysPassed > 0 ? Math.round((double) currentTotal / daysPassed * 10) / 10.0 : 0;

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getId(),
                currentMonthStart,
                currentMonthStart.plusMonths(1).minusDays(1)
        );

        List<MonthlyAnalysisDto.TransactionDetail> transactionDetails = transactions.stream()
                .map(tx -> MonthlyAnalysisDto.TransactionDetail.builder()
                        .date(tx.getTransactionDate())
                        .vendor(tx.getVendor())
                        .amount(tx.getAmount())
                        .build())
                .collect(Collectors.toList());

        // 소비 습관 점수 계산 예시 (하드코딩 / 나중에 계산 로직 연결)
        int currentHabitScore = 88;
        int previousHabitScore = 83;
        int scoreChange = currentHabitScore - previousHabitScore;

        List<String> feedbacks = new ArrayList<>();
        if (scoreChange > 0) {
            feedbacks.add("지난달보다 " + scoreChange + "점 상승했어요!");
        } else if (scoreChange < 0) {
            feedbacks.add("지난달보다 " + Math.abs(scoreChange) + "점 하락했어요.");
        } else {
            feedbacks.add("지난달과 점수가 같아요.");
        }

        if (currentHabitScore >= 90) {
            feedbacks.add("예산 내 소비를 아주 잘 지켰어요!");
            feedbacks.add("모든 항목에서 균형 있게 소비했어요!");
        } else if (currentHabitScore >= 75) {
            feedbacks.add("예산 내 소비를 잘 지켰어요!");
            feedbacks.add("소비가 안정적인 편이에요.");
        } else if (currentHabitScore >= 50) {
            feedbacks.add("조금 더 소비를 조절해봐요.");
        } else {
            feedbacks.add("소비 습관을 개선할 필요가 있어요.");
        }

        feedbacks.add("22일 동안 소비를 기록했어요.");
        feedbacks.add("식비가 전체 소비의 52%를 차지했어요");

        return MonthlyAnalysisDto.builder()
                .currentMonthTotal(currentTotal)
                .currentMonthAverage(average)
                .previousMonthTotal(previousTotal)
                .twoMonthsAgoTotal(twoMonthsAgoTotal)
                .transactions(transactionDetails)
                .habitScore(currentHabitScore)
                .habitScoreChange(scoreChange)
                .habitFeedbackMessages(feedbacks)
                .build();
    }


}
