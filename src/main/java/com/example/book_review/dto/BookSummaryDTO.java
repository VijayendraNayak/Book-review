package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDTO {
    private int id;
    private String title;
    private int price;
    private LocalDate publishedDate;
    private double averageRating;
}