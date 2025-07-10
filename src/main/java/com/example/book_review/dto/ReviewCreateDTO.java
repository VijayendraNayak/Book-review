package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDTO {
    @NotBlank(message = "Comment cannot be blank")
    private String comment;

    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be positive")
    private int bookId;

    // No userId - get from authentication context
    // No createdAt - auto-generated
    // No id - auto-generated
}