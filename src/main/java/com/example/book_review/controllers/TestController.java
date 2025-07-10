package com.example.book_review.controllers;

import com.example.book_review.models.User;
import com.example.book_review.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        System.out.println("=== Public endpoint called successfully ===");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no authentication required");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", auth.isAuthenticated());
        response.put("message", "Authentication successful!");
        response.put("authorities", auth.getAuthorities().toString());
        response.put("username", auth.getName());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin access granted!");
        response.put("authorities", auth.getAuthorities().toString());
        response.put("username", auth.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/author")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<Map<String, Object>> authorEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Author access granted!");
        response.put("authorities", auth.getAuthorities().toString());
        response.put("username", auth.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> userEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User access granted!");
        response.put("authorities", auth.getAuthorities().toString());
        response.put("username", auth.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> results = new HashMap<>();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            String rawPassword = "";
            switch (user.getUsername()) {
                case "admin_user":
                    rawPassword = "admin123";
                    break;
                case "author_user":
                    rawPassword = "author123";
                    break;
                case "regular_user":
                    rawPassword = "user123";
                    break;
            }

            boolean matches = encoder.matches(rawPassword, user.getPassword());
            boolean isBCryptFormat = user.getPassword().startsWith("$2a$") ||
                    user.getPassword().startsWith("$2b$") ||
                    user.getPassword().startsWith("$2y$");

            Map<String, Object> userResult = new HashMap<>();
            userResult.put("username", user.getUsername());
            userResult.put("password", rawPassword);
            userResult.put("actualHashFromDB", user.getPassword());
            userResult.put("matches", matches);
            userResult.put("isBCryptFormat", isBCryptFormat);

            results.put(user.getUsername(), userResult);
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/generate-passwords")
    public ResponseEntity<Map<String, Object>> generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> results = new HashMap<>();

        Map<String, String> passwords = Map.of(
                "admin_user", "admin123",
                "author_user", "author123",
                "regular_user", "user123"
        );

        for (Map.Entry<String, String> entry : passwords.entrySet()) {
            String newHash = encoder.encode(entry.getValue());

            Map<String, String> userResult = new HashMap<>();
            userResult.put("password", entry.getValue());
            userResult.put("newHash", newHash);

            results.put(entry.getKey(), userResult);
        }

        results.put("note", "Use these hashes to update your database if the current ones don't work");

        return ResponseEntity.ok(results);
    }

    @GetMapping("/debug-user/{username}")
    public ResponseEntity<Map<String, Object>> debugUser(@PathVariable String username) {
        System.out.println("=== Debug User Endpoint Called ===");
        System.out.println("Looking for user: " + username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Map<String, Object> response = new HashMap<>();
            response.put("found", true);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("roleId", user.getRole().getId());
            response.put("roleName", user.getRole().getName());
            response.put("passwordHash", user.getPassword().substring(0, 20) + "...");
            response.put("fullPasswordHash", user.getPassword());

            System.out.println("User found successfully: " + user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("found", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
