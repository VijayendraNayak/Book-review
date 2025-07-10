package com.example.book_review.respository;

import com.example.book_review.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre,Integer> {
}
