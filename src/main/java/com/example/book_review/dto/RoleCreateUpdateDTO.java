package com.example.book_review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateUpdateDTO {
    @NotBlank(message = "Role name cannot be blank")
    private String name;

    @NotBlank(message = "Role description cannot be blank")
    private String description;

    // No users - role-user relationships managed separately
}