package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDTO {
    @NotBlank(message = "Comment cannot be blank")
    private String comment;

    // Only comment can be updated
    // User can't change book, user, or timestamps
}