package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Book;
import com.example.book_review.models.Reviews;
import com.example.book_review.models.User;
import com.example.book_review.repository.BookRepository;
import com.example.book_review.repository.ReviewRepository;
import com.example.book_review.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private ModelMapper modelMapper;

    public ReviewResponseDTO createReview(ReviewCreateDTO dto) {
        // Get current authenticated user
        String username = getCurrentUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validate book exists
        Book book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Check if user already reviewed this book
        if (reviewRepo.existsByUserAndBook(user, book)) {
            throw new IllegalArgumentException("You have already reviewed this book");
        }

        Reviews review = new Reviews();
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setBook(book);

        Reviews saved = reviewRepo.save(review);
        return mapToReviewResponse(saved);
    }

    public ReviewResponseDTO updateReview(int reviewId, ReviewUpdateDTO dto) {
        String username = getCurrentUsername();

        Reviews review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        // Check if current user owns this review
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }

        review.setComment(dto.getComment());
        Reviews updated = reviewRepo.save(review);
        return mapToReviewResponse(updated);
    }

    public void deleteReview(int reviewId) {
        String username = getCurrentUsername();

        Reviews review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        // Check if current user owns this review or is admin
        if (!review.getUser().getUsername().equals(username) && !isCurrentUserAdmin()) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        reviewRepo.delete(review);
    }

    public ReviewResponseDTO getReviewById(int reviewId) {
        Reviews review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return mapToReviewResponse(review);
    }

    public Page<ReviewResponseDTO> getAllReviews(Pageable pageable) {
        Page<Reviews> reviews = reviewRepo.findAll(pageable);
        return reviews.map(this::mapToReviewResponse);
    }

    public Page<ReviewResponseDTO> getReviewsByBook(int bookId, Pageable pageable) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Page<Reviews> reviews = reviewRepo.findByBookOrderByCreatedAtDesc(book, pageable);
        return reviews.map(this::mapToReviewResponse);
    }

    public Page<ReviewResponseDTO> getReviewsByUser(String username, Pageable pageable) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Reviews> reviews = reviewRepo.findByUserOrderByCreatedAtDesc(user, pageable);
        return reviews.map(this::mapToReviewResponse);
    }

    public List<ReviewSummaryDTO> getRecentReviewsByBook(int bookId, int limit) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        List<Reviews> reviews = reviewRepo.findTop5ByBookOrderByCreatedAtDesc(book);
        return reviews.stream()
                .limit(limit)
                .map(this::mapToReviewSummary)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToReviewResponse(Reviews review) {
        ReviewResponseDTO dto = modelMapper.map(review, ReviewResponseDTO.class);

        UserSummaryDTO userDTO = new UserSummaryDTO(
                review.getUser().getId(),
                review.getUser().getUsername()
        );
        dto.setUser(userDTO);

        BookSummaryDTO bookDTO = new BookSummaryDTO(
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getBook().getAuthor()
        );
        dto.setBook(bookDTO);

        return dto;
    }

    private ReviewSummaryDTO mapToReviewSummary(Reviews review) {
        ReviewSummaryDTO dto = new ReviewSummaryDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}