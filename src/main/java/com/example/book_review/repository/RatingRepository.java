package com.example.book_review.repository;

import com.example.book_review.models.Book;
import com.example.book_review.models.Rating;
import com.example.book_review.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    // Find rating by user and book
    Optional<Rating> findByUserAndBook(User user, Book book);

    // Find ratings by book ordered by creation date (newest first)
    Page<Rating> findByBookOrderByCreatedAtDesc(Book book, Pageable pageable);

    // Find ratings by user ordered by creation date (newest first)
    Page<Rating> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Calculate average rating for a book
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.book = :book")
    Double findAverageRatingByBook(@Param("book") Book book);

    // Count ratings for a book
    Long countByBook(Book book);

    // Find top 10 recent ratings for a book
    List<Rating> findTop10ByBookOrderByCreatedAtDesc(Book book);
}
