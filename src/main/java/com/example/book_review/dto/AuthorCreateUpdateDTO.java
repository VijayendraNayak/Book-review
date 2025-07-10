package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCreateUpdateDTO {
    @NotBlank(message = "Author name is required")
    private String name;

    @NotBlank(message = "Author bio is required")
    private String bio;

    @NotBlank(message = "Author nationality is required")
    private String nationality;

    private List<Integer> bookIds; // Optional for linking existing books
}

