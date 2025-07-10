package com.example.book_review.respository;

import com.example.book_review.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
}
