package com.example.book_review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingCreateUpdateDTO {
    @Min(value = 1, message = "Minimum rating must be 1")
    @Max(value = 5, message = "Maximum rating must be 5")
    private int stars;

    @NotNull(message = "Book ID is required")
    private int bookId;

}