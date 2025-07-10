package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDTO {
    private int id;
    private String comment;
    private LocalDateTime createdAt;
    private String username;
    // No full user/book objects to keep it lightweight
}