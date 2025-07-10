package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.RatingService;
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
@RequestMapping("/api/ratings")
@Tag(name = "Rating Management", description = "APIs for managing book ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping
    @Operation(summary = "Get all ratings")
    public ResponseEntity<Page<RatingSummaryDTO>> getAllRatings(Pageable pageable) {
        Page<RatingSummaryDTO> ratings = ratingService.getAllRatings(pageable);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID")
    public ResponseEntity<RatingResponseDTO> getRatingById(@PathVariable Long id) {
        RatingResponseDTO rating = ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }

    @PostMapping
    @Operation(summary = "Create a new rating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponseDTO> createRating(
            @Valid @RequestBody RatingCreateUpdateDTO ratingDTO,
            Authentication authentication) {
        RatingResponseDTO rating = ratingService.createRating(ratingDTO, authentication.getName());
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponseDTO> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingCreateUpdateDTO ratingDTO,
            Authentication authentication) {
        RatingResponseDTO rating = ratingService.updateRating(id, ratingDTO, authentication.getName());
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id, Authentication authentication) {
        ratingService.deleteRating(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get ratings by book")
    public ResponseEntity<List<RatingSummaryDTO>> getRatingsByBook(@PathVariable Long bookId) {
        List<RatingSummaryDTO> ratings = ratingService.getRatingsByBook(bookId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/book/{bookId}/average")
    @Operation(summary = "Get average rating for a book")
    public ResponseEntity<Double> getAverageRatingForBook(@PathVariable Long bookId) {
        Double averageRating = ratingService.getAverageRatingForBook(bookId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get ratings by user")
    public ResponseEntity<List<RatingSummaryDTO>> getRatingsByUser(@PathVariable Long userId) {
        List<RatingSummaryDTO> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/my-ratings")
    @Operation(summary = "Get current user's ratings")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RatingSummaryDTO>> getCurrentUserRatings(Authentication authentication) {
        List<RatingSummaryDTO> ratings = ratingService.getRatingsByUsername(authentication.getName());
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/book/{bookId}/user")
    @Operation(summary = "Get current user's rating for a book")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponseDTO> getUserRatingForBook(
            @PathVariable Long bookId,
            Authentication authentication) {
        RatingResponseDTO rating = ratingService.getUserRatingForBook(bookId, authentication.getName());
        return ResponseEntity.ok(rating);
    }
}
