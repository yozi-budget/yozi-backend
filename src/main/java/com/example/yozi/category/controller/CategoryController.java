package com.example.yozi.category.controller;

import com.example.yozi.category.dto.CategoryDto;
import com.example.yozi.category.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Operation(summary = "카테고리 목록 조회", description = "모든 카테고리를 ID 오름차순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CategoryDto> list = categoryRepository.findAllByOrderByIdAsc()
                .stream()
                .map(c -> new CategoryDto(c.getType(), c.getDisplayName()))
                .toList();

        return ResponseEntity.ok(list);
    }
}

