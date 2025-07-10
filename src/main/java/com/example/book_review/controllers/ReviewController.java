package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "APIs for managing book reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all reviews")
    public ResponseEntity<Page<ReviewSummaryDTO>> getAllReviews(Pageable pageable) {
        Page<ReviewSummaryDTO> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PostMapping
    @Operation(summary = "Create a new review")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewCreateDTO reviewDTO,
            Authentication authentication) {
        ReviewResponseDTO review = reviewService.createReview(reviewDTO, authentication.getName());
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateDTO reviewDTO,
            Authentication authentication) {
        ReviewResponseDTO review = reviewService.updateReview(id, reviewDTO, authentication.getName());
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, Authentication authentication) {
        reviewService.deleteReview(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get reviews by book")
    public ResponseEntity<List<ReviewSummaryDTO>> getReviewsByBook(@PathVariable Long bookId) {
        List<ReviewSummaryDTO> reviews = reviewService.getReviewsByBook(bookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<List<ReviewSummaryDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewSummaryDTO> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Get current user's reviews")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewSummaryDTO>> getCurrentUserReviews(Authentication authentication) {
        List<ReviewSummaryDTO> reviews = reviewService.getReviewsByUsername(authentication.getName());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/search")
    @Operation(summary = "Search reviews by content")
    public ResponseEntity<List<ReviewSummaryDTO>> searchReviews(@RequestParam String query) {
        List<ReviewSummaryDTO> reviews = reviewService.searchReviews(query);
        return ResponseEntity.ok(reviews);
    }
}
