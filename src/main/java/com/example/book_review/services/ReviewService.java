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
import org.springframework.stereotype.Service;

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

    public Page<ReviewSummaryDTO> getAllReviews(Pageable pageable) {
        Page<Reviews> reviews = reviewRepo.findAll(pageable);
        return reviews.map(this::mapToReviewSummary);
    }

    public ReviewResponseDTO getReviewById(Long id) {
        Reviews review = reviewRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return mapToReviewResponse(review);
    }

    public ReviewResponseDTO createReview(ReviewCreateDTO dto, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

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

    public ReviewResponseDTO updateReview(Long id, ReviewUpdateDTO dto, String username) {
        Reviews review = reviewRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }

        review.setComment(dto.getComment());
        Reviews updated = reviewRepo.save(review);
        return mapToReviewResponse(updated);
    }

    public void deleteReview(Long id, String username) {
        Reviews review = reviewRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        reviewRepo.delete(review);
    }

    public List<ReviewSummaryDTO> getReviewsByBook(Long bookId) {
        Book book = bookRepo.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        return reviewRepo.findByBookOrderByCreatedAtDesc(book).stream()
                .map(this::mapToReviewSummary)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryDTO> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return reviewRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToReviewSummary)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryDTO> getReviewsByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return reviewRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToReviewSummary)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryDTO> searchReviews(String query) {
        List<Reviews> reviews = reviewRepo.findByCommentContainingIgnoreCase(query);
        return reviews.stream()
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

        BookSummaryDTO bookDTO = new BookSummaryDTO();
        bookDTO.setId(review.getBook().getId());
        bookDTO.setTitle(review.getBook().getTitle());
        bookDTO.setAuthor(review.getBook().getAuthor());
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
}
