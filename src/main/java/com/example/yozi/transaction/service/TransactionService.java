package com.example.yozi.transaction.service;

import com.example.yozi.category.repository.CategoryRepository;
import com.example.yozi.transaction.dto.TransactionDto;
import com.example.yozi.transaction.dto.TransactionResponseDto;
import com.example.yozi.transaction.entity.Transaction;
import com.example.yozi.transaction.entity.enums.TransactionType;
import com.example.yozi.transaction.repository.TransactionRepository;
import com.example.yozi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.yozi.category.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public List<TransactionResponseDto> getAllTransactionsByUser(User user) {
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> getTransactionsByUserAndType(User user, String type) {
        List<Transaction> transactions;

        if ("expense".equalsIgnoreCase(type)) {
            transactions = transactionRepository.findByUserIdAndType(user.getId(), TransactionType.EXPENSE);
        } else if ("income".equalsIgnoreCase(type)) {
            transactions = transactionRepository.findByUserIdAndType(user.getId(), TransactionType.INCOME);
        } else {
            transactions = transactionRepository.findByUserId(user.getId());
        }

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto createTransaction(User user, TransactionDto dto) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(dto.getType())
                .categoryId(dto.getCategoryId())
                .paymentMethod(dto.getPaymentMethod())
                .vendor(dto.getVendor())
                .amount(dto.getAmount())
                .memo(dto.getMemo())
                .transactionDate(dto.getTransactionDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return convertToDto(saved);
    }

    public TransactionResponseDto updateTransaction(User user, Long transactionId, TransactionDto dto) {
        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // 거래 소유자 확인
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Transaction does not belong to user " + user.getId());
        }

        existing.setType(dto.getType());
        existing.setCategoryId(dto.getCategoryId());
        existing.setPaymentMethod(dto.getPaymentMethod());
        existing.setVendor(dto.getVendor());
        existing.setAmount(dto.getAmount());
        existing.setMemo(dto.getMemo());
        existing.setTransactionDate(dto.getTransactionDate());

        Transaction updated = transactionRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteTransaction(User user, Long transactionId) {
        Transaction transactionToDelete = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        if (!transactionToDelete.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Transaction does not belong to user " + user.getId());
        }

        transactionRepository.deleteById(transactionId);
    }

    private TransactionResponseDto convertToDto(Transaction tx) {
        String categoryDisplayName = categoryRepository.findById(tx.getCategoryId().longValue())
                .map(Category::getDisplayName)
                .orElse("알 수 없는 카테고리");

        return new TransactionResponseDto(
                tx.getId(),
                tx.getUser().getNickname(),
                tx.getType(),
                tx.getCategoryId(),
                categoryDisplayName,
                tx.getPaymentMethod(),
                tx.getVendor(),
                tx.getAmount(),
                tx.getMemo(),
                tx.getTransactionDate()
        );
    }

    public List<TransactionResponseDto> getTransactionsByUserAndCategory(User user, Integer categoryId) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
