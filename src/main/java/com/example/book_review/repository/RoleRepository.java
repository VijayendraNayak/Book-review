package com.example.book_review.repository;

import com.example.book_review.models.Roles;
import com.example.book_review.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles,Integer> {
    Optional<Roles> findByName(String rolename);

    boolean existsByName(String rolename); // ✅ checks if username exists

    // Search roles by name containing text (case-insensitive) - needed by RoleService
    List<Roles> findByNameContainingIgnoreCase(String name);
}
