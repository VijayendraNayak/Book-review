package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private int id;
    private int stars;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSummaryDTO user;
    private BookSummaryDTO book;
}