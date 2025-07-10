package com.example.book_review.respository;

import com.example.book_review.models.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Reviews,Integer> {
}
