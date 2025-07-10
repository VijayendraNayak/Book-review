package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// For responses with book details
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponseDTO {
    private int id;
    private String name;
    private String description;
    private List<BookSummaryDTO> books;
    private int bookCount;
}
