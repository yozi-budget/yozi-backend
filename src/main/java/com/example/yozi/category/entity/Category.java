package com.example.yozi.category.entity;

import com.example.yozi.category.entity.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private CategoryType type;

    @Column(nullable = false)
    private String displayName;

    public Category(CategoryType type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }
}
