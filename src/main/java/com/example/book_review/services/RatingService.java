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

    public Page<RatingSummaryDTO> getAllRatings(Pageable pageable) {
        Page<Rating> ratings = ratingRepo.findAll(pageable);
        return ratings.map(this::mapToRatingSummary);
    }

    public RatingResponseDTO getRatingById(Long id) {
        Rating rating = ratingRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));
        return mapToRatingResponse(rating);
    }

    public RatingResponseDTO createRating(RatingCreateUpdateDTO dto, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Check if user already rated this book
        Optional<Rating> existingRating = ratingRepo.findByUserAndBook(user, book);

        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setStars(dto.getStars());
        } else {
            rating = new Rating();
            rating.setStars(dto.getStars());
            rating.setUser(user);
            rating.setBook(book);
        }

        Rating saved = ratingRepo.save(rating);
        return mapToRatingResponse(saved);
    }

    public RatingResponseDTO updateRating(Long id, RatingCreateUpdateDTO dto, String username) {
        Rating rating = ratingRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));

        if (!rating.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only update your own ratings");
        }

        rating.setStars(dto.getStars());
        Rating updated = ratingRepo.save(rating);
        return mapToRatingResponse(updated);
    }

    public void deleteRating(Long id, String username) {
        Rating rating = ratingRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));

        if (!rating.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own ratings");
        }

        ratingRepo.delete(rating);
    }

    public List<RatingSummaryDTO> getRatingsByBook(Long bookId) {
        Book book = bookRepo.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return ratingRepo.findByBookOrderByCreatedAtDesc(book).stream()
                .map(this::mapToRatingSummary)
                .collect(Collectors.toList());
    }

    public Double getAverageRatingForBook(Long bookId) {
        Book book = bookRepo.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return ratingRepo.findAverageRatingByBook(book);
    }

    public List<RatingSummaryDTO> getRatingsByUser(Long userId) {
        User user = userRepo.findById(userId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ratingRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToRatingSummary)
                .collect(Collectors.toList());
    }

    public List<RatingSummaryDTO> getRatingsByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ratingRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToRatingSummary)
                .collect(Collectors.toList());
    }

    public RatingResponseDTO getUserRatingForBook(Long bookId, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepo.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Rating rating = ratingRepo.findByUserAndBook(user, book)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));

        return mapToRatingResponse(rating);
    }

    private RatingResponseDTO mapToRatingResponse(Rating rating) {
        RatingResponseDTO dto = modelMapper.map(rating, RatingResponseDTO.class);

        UserSummaryDTO userDTO = new UserSummaryDTO(
                rating.getUser().getId(),
                rating.getUser().getUsername()
        );
        dto.setUser(userDTO);

        BookSummaryDTO bookDTO = new BookSummaryDTO();
        bookDTO.setId(rating.getBook().getId());
        bookDTO.setTitle(rating.getBook().getTitle());
        bookDTO.setAuthor(rating.getBook().getAuthor());
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
}
