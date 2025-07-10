package com.example.book_review.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no authentication required");
        response.put("timestamp", System.currentTimeMillis());
        response.put("access", "PUBLIC");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-summary")
    public ResponseEntity<Map<String, Object>> getTestSummary() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "Book Review API Security Testing Guide");

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("admin", "admin_user:admin123");
        credentials.put("author", "author_user:author123");
        credentials.put("user", "regular_user:user123");
        response.put("credentials", credentials);

        Map<String, Object> hierarchy = new HashMap<>();
        hierarchy.put("ADMIN", "Full system access - can manage users, roles, and all content");
        hierarchy.put("AUTHOR", "Content management - can create/edit books, authors, genres");
        hierarchy.put("USER", "Basic access - can read content and manage own reviews/ratings");
        response.put("hierarchy", hierarchy);

        Map<String, Object> testEndpoints = new HashMap<>();
        testEndpoints.put("public", Arrays.asList("/api/test/public", "/api/test/test-summary"));
        testEndpoints.put("admin_only", Arrays.asList("/api/test/admin", "/api/users", "/api/roles"));
        testEndpoints.put("author_and_admin", Arrays.asList("/api/test/author", "POST /api/books", "POST /api/authors", "POST /api/genres"));
        testEndpoints.put("all_authenticated", Arrays.asList("/api/test/user", "GET /api/books", "/api/reviews", "/api/ratings"));
        response.put("testEndpoints", testEndpoints);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("access", "AUTHENTICATED");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome Admin! You have full system access.");
        response.put("username", auth.getName());
        response.put("role", "ADMIN");
        response.put("permissions", Arrays.asList("Manage Users", "Manage Roles", "Full Content Access"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Map<String, Object>> authorEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome! You have content management access.");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("permissions", Arrays.asList("Create Books", "Manage Authors", "Manage Genres"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> userEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome! You have basic user access.");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("permissions", Arrays.asList("Read Books", "Write Reviews", "Rate Books"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminOnlyHierarchy() {
        Map<String, Object> response = new HashMap<>();
        response.put("level", "ADMIN ONLY");
        response.put("message", "This endpoint is restricted to ADMIN users only");
        response.put("expectedUsers", Arrays.asList("admin_user"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy/author-and-above")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Map<String, Object>> authorAndAboveHierarchy() {
        Map<String, Object> response = new HashMap<>();
        response.put("level", "AUTHOR AND ABOVE");
        response.put("message", "This endpoint is accessible to ADMIN and AUTHOR users");
        response.put("expectedUsers", Arrays.asList("admin_user", "author_user"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy/user-and-above")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> userAndAboveHierarchy() {
        Map<String, Object> response = new HashMap<>();
        response.put("level", "USER AND ABOVE");
        response.put("message", "This endpoint is accessible to all authenticated users");
        response.put("expectedUsers", Arrays.asList("admin_user", "author_user", "regular_user"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/functionality/book-write")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Map<String, Object>> testBookWrite(@RequestBody(required = false) Map<String, Object> bookData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book write access confirmed!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("operation", "CREATE_BOOK");
        response.put("receivedData", bookData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/functionality/book-read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> testBookRead() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book read access confirmed!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("operation", "READ_BOOKS");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/functionality/review-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> testReviewAccess(@RequestBody(required = false) Map<String, Object> reviewData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Review access confirmed!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("operation", "MANAGE_REVIEWS");
        response.put("receivedData", reviewData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/functionality/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testAdminOnlyFunctionality() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin-only functionality access confirmed!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("operation", "ADMIN_FUNCTIONS");
        response.put("capabilities", Arrays.asList("User Management", "Role Management", "System Configuration"));
        return ResponseEntity.ok(response);
    }
}
