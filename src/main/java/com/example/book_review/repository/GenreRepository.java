package com.example.book_review.repository;

import com.example.book_review.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre,Integer> {

    // Check if genre exists by name (case-insensitive)
    boolean existsByNameIgnoreCase(String name);

    // Check if genre exists by name excluding current genre (for updates)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

    // Find all genres ordered by name
    List<Genre> findAllByOrderByNameAsc();

    // Search genres by name containing text - returns List for service compatibility
    List<Genre> findByNameContainingIgnoreCaseOrderByName(String name);

    // Find genres ordered by book count (most popular first)
    @Query("SELECT g FROM Genre g LEFT JOIN g.books b GROUP BY g ORDER BY COUNT(b) DESC")
    List<Genre> findGenresOrderByBookCountDesc();

    // Find genre with books loaded
    @Query("SELECT g FROM Genre g LEFT JOIN FETCH g.books WHERE g.id = :id")
    Optional<Genre> findByIdWithBooks(@Param("id") Integer id);
}
