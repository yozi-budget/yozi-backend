package com.example.yozi.budget.controller;

import com.example.yozi.budget.dto.*;
import com.example.yozi.budget.service.BudgetService;
import com.example.yozi.jwt.UserPrincipal;
import com.example.yozi.user.entity.User;
import com.example.yozi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Budget API", description = "예산 관련 기능 API (등록, 조회, 요약 등)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @Operation(summary = "예산 설정", description = "사용자가 특정 월에 대해 카테고리별 예산을 설정합니다.")
    @PostMapping
    public ResponseEntity<Void> setBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody List<BudgetRequestDto> budgets) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        budgetService.setBudget(user, date, budgets);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예산 조회", description = "사용자의 특정 월 예산 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BudgetResponseDto>> getBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        List<BudgetResponseDto> budgets = budgetService.getBudget(user, date);
        return ResponseEntity.ok(budgets);
    }

    @Operation(summary = "총 예산 조회", description = "사용자의 특정 월 총 예산 금액을 반환합니다.")
    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        int totalBudget = budgetService.calculateTotalBudget(user, date);
        return ResponseEntity.ok(totalBudget);
    }

    @Operation(summary = "총 지출 금액 조회", description = "특정 월의 총 지출 금액을 조회합니다.")
    @GetMapping("/spent")
    public ResponseEntity<Integer> getSpentAmount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        int spentAmount = budgetService.calculateExpenseAmount(user, date);
        return ResponseEntity.ok(spentAmount);
    }

    @Operation(summary = "총 수입 금액 조회", description = "특정 월의 총 수입 금액을 조회합니다.")
    @GetMapping("/income")
    public ResponseEntity<Integer> getIncomeAmount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        int incomeAmount = budgetService.calculateIncomeAmount(user, date);
        return ResponseEntity.ok(incomeAmount);
    }

    @Operation(summary = "남은 예산 금액 조회", description = "특정 월의 남은 예산 금액을 조회합니다.")
    @GetMapping("/remaining")
    public ResponseEntity<Integer> getRemainingBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        int remaining = budgetService.calculateRemainingBudget(user, date);
        return ResponseEntity.ok(remaining);
    }

    @Operation(summary = "초과 예산 금액 조회", description = "특정 월의 초과 예산 금액을 조회합니다.")
    @GetMapping("/exceeded")
    public ResponseEntity<Integer> getExceededBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        int exceeded = budgetService.calculateExceededBudget(user, date);
        return ResponseEntity.ok(exceeded);
    }

    @Operation(summary = "예산 페이지", description = "예산 페이지 로드")
    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryDto> getBudgetSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        BudgetSummaryDto summary = budgetService.getBudgetSummary(user, date);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "메인 요약 정보 조회", description = "현재 달 기준 사용자의 메인 화면 요약 정보를 조회합니다.")
    @GetMapping("/main/summary")
    public ResponseEntity<MainSummaryDto> getMainSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        MainSummaryDto summary = budgetService.getMainSummary(user);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "일별 수입/지출 조회", description = "특정 월의 날짜별 수입/지출 금액을 조회합니다.")
    @GetMapping("/main/daily-amounts")
    public ResponseEntity<List<DailyAmountDto>> getDailyAmounts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        List<DailyAmountDto> dailyAmounts = budgetService.getDailyIncomeExpense(user, date);
        return ResponseEntity.ok(dailyAmounts);
    }

    @Operation(summary = "월별 지출 분석 조회",
            description = "현재 달의 총 지출, 평균 하루 지출, 전월 및 전전월 총 지출 금액과 이번달 거래 내역을 반환합니다.")
    @GetMapping("/analysis/monthly")
    public ResponseEntity<MonthlyAnalysisDto> getMonthlyAnalysis(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        MonthlyAnalysisDto dto = budgetService.getMonthlyAnalysis(user);
        return ResponseEntity.ok(dto);
    }
}
