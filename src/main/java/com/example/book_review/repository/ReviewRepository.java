package com.example.book_review.repository;

import com.example.book_review.models.Book;
import com.example.book_review.models.Reviews;
import com.example.book_review.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews,Integer> {

    // Check if user already reviewed a book
    boolean existsByUserAndBook(User user, Book book);

    // Find reviews by book ordered by creation date (newest first) - List for service compatibility
    List<Reviews> findByBookOrderByCreatedAtDesc(Book book);

    // Find reviews by user ordered by creation date (newest first) - List for service compatibility
    List<Reviews> findByUserOrderByCreatedAtDesc(User user);

    // Find top 5 recent reviews for a book
    List<Reviews> findTop5ByBookOrderByCreatedAtDesc(Book book);

    // Search reviews by comment containing text (case-insensitive)
    List<Reviews> findByCommentContainingIgnoreCase(String query);
}
