package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Book;
import com.example.book_review.models.Rating;
import com.example.book_review.models.User;
import com.example.book_review.repository.BookRepository;
import com.example.book_review.repository.RatingRepository;
import com.example.book_review.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private ModelMapper modelMapper;

    public RatingResponseDTO createOrUpdateRating(RatingCreateUpdateDTO dto) {
        String username = getCurrentUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Check if user already rated this book
        Optional<Rating> existingRating = ratingRepo.findByUserAndBook(user, book);

        Rating rating;
        if (existingRating.isPresent()) {
            // Update existing rating
            rating = existingRating.get();
            rating.setStars(dto.getStars());
        } else {
            // Create new rating
            rating = new Rating();
            rating.setStars(dto.getStars());
            rating.setUser(user);
            rating.setBook(book);
        }

        Rating saved = ratingRepo.save(rating);
        return mapToRatingResponse(saved);
    }

    public void deleteRating(int ratingId) {
        String username = getCurrentUsername();

        Rating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));

        // Check if current user owns this rating or is admin
        if (!rating.getUser().getUsername().equals(username) && !isCurrentUserAdmin()) {
            throw new IllegalArgumentException("You can only delete your own ratings");
        }

        ratingRepo.delete(rating);
    }

    public RatingResponseDTO getRatingById(int ratingId) {
        Rating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));
        return mapToRatingResponse(rating);
    }

    public Optional<RatingResponseDTO> getUserRatingForBook(int bookId) {
        String username = getCurrentUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return ratingRepo.findByUserAndBook(user, book)
                .map(this::mapToRatingResponse);
    }

    public Page<RatingResponseDTO> getAllRatings(Pageable pageable) {
        Page<Rating> ratings = ratingRepo.findAll(pageable);
        return ratings.map(this::mapToRatingResponse);
    }

    public Page<RatingResponseDTO> getRatingsByBook(int bookId, Pageable pageable) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Page<Rating> ratings = ratingRepo.findByBookOrderByCreatedAtDesc(book, pageable);
        return ratings.map(this::mapToRatingResponse);
    }

    public Page<RatingResponseDTO> getRatingsByUser(String username, Pageable pageable) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Rating> ratings = ratingRepo.findByUserOrderByCreatedAtDesc(user, pageable);
        return ratings.map(this::mapToRatingResponse);
    }

    public Double getAverageRatingForBook(int bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return ratingRepo.findAverageRatingByBook(book);
    }

    public Long getRatingCountForBook(int bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return ratingRepo.countByBook(book);
    }

    public List<RatingSummaryDTO> getRecentRatingsByBook(int bookId, int limit) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        List<Rating> ratings = ratingRepo.findTop10ByBookOrderByCreatedAtDesc(book);
        return ratings.stream()
                .limit(limit)
                .map(this::mapToRatingSummary)
                .collect(Collectors.toList());
    }

    private RatingResponseDTO mapToRatingResponse(Rating rating) {
        RatingResponseDTO dto = modelMapper.map(rating, RatingResponseDTO.class);

        UserSummaryDTO userDTO = new UserSummaryDTO(
                rating.getUser().getId(),
                rating.getUser().getUsername()
        );
        dto.setUser(userDTO);

        BookSummaryDTO bookDTO = new BookSummaryDTO(
                rating.getBook().getId(),
                rating.getBook().getTitle(),
                rating.getBook().getAuthor()
        );
        dto.setBook(bookDTO);

        return dto;
    }

    private RatingSummaryDTO mapToRatingSummary(Rating rating) {
        RatingSummaryDTO dto = new RatingSummaryDTO();
        dto.setId(rating.getId());
        dto.setStars(rating.getStars());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUsername(rating.getUser().getUsername());
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