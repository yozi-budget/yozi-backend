package com.example.yozi.transaction.controller;

import com.example.yozi.jwt.UserPrincipal;
import com.example.yozi.transaction.dto.TransactionDto;
import com.example.yozi.transaction.dto.TransactionResponseDto;
import com.example.yozi.transaction.service.TransactionService;
import com.example.yozi.user.entity.User;
import com.example.yozi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transaction", description = "가계부 거래 내역 API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;  // UserService 주입 추가

    private User getUserFromPrincipal(UserPrincipal userPrincipal) {
        return userService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));
    }

    @GetMapping(params = "!type")
    public ResponseEntity<List<TransactionResponseDto>> getAllByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = getUserFromPrincipal(userPrincipal);
        List<TransactionResponseDto> dtos = transactionService.getAllTransactionsByUser(user);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(params = "type")
    public ResponseEntity<List<TransactionResponseDto>> getByUserAndType(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String type) {
        User user = getUserFromPrincipal(userPrincipal);
        List<TransactionResponseDto> dtos;

        if ("expense".equalsIgnoreCase(type)) {
            dtos = transactionService.getTransactionsByUserAndType(user, "expense");
        } else if ("income".equalsIgnoreCase(type)) {
            dtos = transactionService.getTransactionsByUserAndType(user, "income");
        } else {
            dtos = transactionService.getAllTransactionsByUser(user);
        }

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "새 가게부 생성", description = "현재 인증된 사용자의 새로운 가게부를 생성합니다.")
    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody TransactionDto dto) {
        User user = getUserFromPrincipal(userPrincipal);
        TransactionResponseDto created = transactionService.createTransaction(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "가게부 내역 수정", description = "현재 인증된 사용자의 특정 가게부 내역을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody TransactionDto dto) {
        User user = getUserFromPrincipal(userPrincipal);
        TransactionResponseDto updated = transactionService.updateTransaction(user, id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "가게부 내역 삭제", description = "현재 인증된 사용자의 특정 가게부 내역을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = getUserFromPrincipal(userPrincipal);
        transactionService.deleteTransaction(user, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리별 가게부 내역 조회", description = "현재 인증된 사용자의 특정 카테고리 가게부 내역을 조회합니다.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponseDto>> getByUserAndCategory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer categoryId) {
        User user = getUserFromPrincipal(userPrincipal);
        List<TransactionResponseDto> dtos = transactionService.getTransactionsByUserAndCategory(user, categoryId);
        return ResponseEntity.ok(dtos);
    }
}
