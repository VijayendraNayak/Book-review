package com.example.book_review.dto;

import lombok.Data;

import java.util.List;
@Data
// For responses
public class AuthorResponseDTO {
    private int id;
    private String name;
    private String bio;
    private String nationality;
    private List<BookSummaryDTO> books;
}
