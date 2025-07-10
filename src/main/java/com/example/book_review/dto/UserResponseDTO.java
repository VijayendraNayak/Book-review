package com.example.book_review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private int id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private List<RoleSummaryDTO> roles;
    private boolean isActive;

    // NO PASSWORD FIELD - EVER!
}