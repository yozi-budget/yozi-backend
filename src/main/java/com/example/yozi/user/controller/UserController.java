/*
package com.example.yozi.user.controller;

import com.example.yozi.user.entity.enums.SocialType;
import com.example.yozi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.example.yozi.user.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/social")
    public ResponseEntity<User> getBySocial(@RequestParam String socialId, @RequestParam SocialType type) {
        Optional<User> user = userService.findBySocial(socialId, type);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
*/