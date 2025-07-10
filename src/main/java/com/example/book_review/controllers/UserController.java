package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user registration, authentication and profile management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO user = userService.register(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(Authentication authentication) {
        UserProfileDTO profile = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @Valid @RequestBody UserProfileDTO profileDTO,
            Authentication authentication) {
        UserProfileDTO updatedProfile = userService.updateUserProfile(authentication.getName(), profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserSummaryDTO>> getAllUsers(Pageable pageable) {
        Page<UserSummaryDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Update user roles (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody RoleCreateUpdateDTO roleDTO) {
        UserResponseDTO user = userService.updateUserRoles(id, roleDTO);
        return ResponseEntity.ok(user);
    }
}
