package com.example.yozi.user.service;

import com.example.yozi.user.entity.User;
import com.example.yozi.user.entity.enums.SocialType;
import com.example.yozi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + id));
    }

    public Optional<User> findBySocial(String socialId, SocialType socialType) {
        return userRepository.findBySocialIdAndSocialType(socialId, socialType);
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }
}
