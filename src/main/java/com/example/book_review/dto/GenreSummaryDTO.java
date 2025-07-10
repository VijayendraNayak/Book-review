package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// For simple references (used in BookResponseDTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreSummaryDTO {
    private int id;
    private String name;
}
