package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private int id;
    private String title;
    private String description;
    private int price;
    private LocalDate publishedDate;
    private List<GenreSummaryDTO> genres;
    private List<AuthorSummaryDTO> authors;
    private double averageRating;
    private int totalReviews;
}