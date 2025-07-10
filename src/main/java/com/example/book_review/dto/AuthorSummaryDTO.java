package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// For lists/references
public class AuthorSummaryDTO {
    private int id;
    private String name;
    private String nationality;
}
