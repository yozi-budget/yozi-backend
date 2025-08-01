package com.example.yozi.user.repository;

import com.example.yozi.user.entity.User;
import com.example.yozi.user.entity.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
