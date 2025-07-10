package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// For creating/updating genres
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreCreateUpdateDTO {
    @NotBlank(message = "Genre name cannot be blank")
    private String name;

    @NotBlank(message = "Genre description cannot be blank")
    private String description;
    // No books - genres are usually created independently
}

