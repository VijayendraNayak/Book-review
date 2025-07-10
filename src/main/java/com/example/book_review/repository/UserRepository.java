package com.example.book_review.repository;

import com.example.book_review.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username); // ✅ checks if username exists

    boolean existsByEmail(String email);
}
