package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDTO {
    private int id;
    private String title;
    private String author; // Comma-separated author names
    private Integer price;
    private LocalDate publishedDate;
    private Double averageRating;

    // Constructor for basic book info (used in reviews/ratings)
    public BookSummaryDTO(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}
