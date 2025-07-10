package com.example.book_review.dto;

import java.util.List;

// For responses
public class AuthorResponseDTO {
    private int id;
    private String name;
    private String bio;
    private String nationality;
    private List<BookSummaryDTO> books;
}
