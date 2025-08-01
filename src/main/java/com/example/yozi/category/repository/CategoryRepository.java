package com.example.yozi.category.repository;

import com.example.yozi.category.entity.Category;
import com.example.yozi.category.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByType(CategoryType type);
    List<Category> findAllByOrderByIdAsc();
}
