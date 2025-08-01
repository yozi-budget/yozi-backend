package com.example.yozi.category.dto;

import com.example.yozi.category.entity.enums.CategoryType;

public record CategoryDto(CategoryType type, String displayName) {}
