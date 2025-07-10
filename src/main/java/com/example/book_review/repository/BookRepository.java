package com.example.book_review.repository;

import com.example.book_review.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    // Find books within a price range
    List<Book> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // Find books by title containing text (case-insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
}
