package com.example.book_review.repository;

import com.example.book_review.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    // 🔍 Find by name (partial match, case-insensitive)
    List<Author> findByNameContainingIgnoreCase(String name);

    // 🔍 Find by nationality (case-insensitive)
    List<Author> findByNationalityIgnoreCase(String nationality);
}