package com.example.book_review.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword; // Optional - only if changing password

    private String currentPassword; // Required if changing password

    // No username change (usually immutable)
    // No roleId (only admins can change roles)
}