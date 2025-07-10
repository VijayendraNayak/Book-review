package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleSummaryDTO {
    private int id;
    private String name;
    // Used in UserResponseDTO to show user's roles
}