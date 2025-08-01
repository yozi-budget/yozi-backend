package com.example.yozi.category.entity.enums;

public enum CategoryType {
    FOOD_DINING("식료품/외식"),
    HOUSING_UTILITIES("주거/공과금"),
    TRANSPORTATION("교통/차량"),
    SHOPPING_FASHION("쇼핑/패션"),
    HEALTH_MEDICAL("건강/의료"),
    EDUCATION("교육/자기개발"),
    LEISURE_CULTURE("여가/문화"),
    FINANCE_OTHERS("금융/기타");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}