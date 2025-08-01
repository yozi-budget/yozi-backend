package com.example.yozi.category.service;

import com.example.yozi.category.entity.Category;
import com.example.yozi.category.entity.enums.CategoryType;
import com.example.yozi.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void initializeCategories() {
        for (CategoryType type : CategoryType.values()) {
            categoryRepository.findByType(type).orElseGet(() ->
                    categoryRepository.save(new Category(type, type.getDisplayName()))
            );
        }
    }
}

