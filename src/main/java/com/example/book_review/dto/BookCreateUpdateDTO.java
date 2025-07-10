package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateUpdateDTO {
    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotBlank(message = "Description can't be blank")
    private String description;

    @Positive(message = "Price must be positive")
    private int price;

    @NotNull(message = "Published date is required")
    @PastOrPresent(message = "Published date must be past or today")
    private LocalDate publishedDate;

    private List<Integer> genreIds;
    private List<Integer> authorIds;
}