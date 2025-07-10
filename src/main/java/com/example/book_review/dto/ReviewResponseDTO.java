package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private int id;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSummaryDTO user;
    private BookSummaryDTO book;
}