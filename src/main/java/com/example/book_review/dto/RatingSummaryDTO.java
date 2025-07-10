package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryDTO {
    private int id;
    private int stars;
    private LocalDateTime createdAt;
    private String username;
    // No full user/book objects to keep it lightweight
}