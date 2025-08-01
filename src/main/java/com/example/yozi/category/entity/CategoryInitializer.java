package com.example.yozi.category.entity;

import com.example.yozi.category.entity.enums.CategoryType;
import com.example.yozi.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local") // 💡 이 라인이 핵심!
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        for (CategoryType type : CategoryType.values()) {
            categoryRepository.findByType(type).orElseGet(() ->
                    categoryRepository.save(new Category(type, getDisplayName(type)))
            );
        }
    }

    private String getDisplayName(CategoryType type) {
        return switch (type) {
            case FOOD_DINING -> "식료품/외식";
            case HOUSING_UTILITIES -> "주거/공과금";
            case TRANSPORTATION -> "교통/차량";
            case SHOPPING_FASHION -> "쇼핑/패션";
            case HEALTH_MEDICAL -> "건강/의료";
            case EDUCATION -> "교육/자기개발";
            case LEISURE_CULTURE -> "여가/문화";
            case FINANCE_OTHERS -> "금융/기타";
        };
    }
}
