package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Author Management", description = "APIs for managing authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors")
    public ResponseEntity<Page<AuthorSummaryDTO>> getAllAuthors(Pageable pageable) {
        Page<AuthorSummaryDTO> authors = authorService.getAllAuthors(pageable);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) {
        AuthorResponseDTO author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @PostMapping
    @Operation(summary = "Create a new author")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<AuthorResponseDTO> createAuthor(@Valid @RequestBody AuthorCreateUpdateDTO authorDTO) {
        AuthorResponseDTO author = authorService.createAuthor(authorDTO);
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update author")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorCreateUpdateDTO authorDTO) {
        AuthorResponseDTO author = authorService.updateAuthor(id, authorDTO);
        return ResponseEntity.ok(author);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete author")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors by name")
    public ResponseEntity<List<AuthorSummaryDTO>> searchAuthorsByName(@RequestParam String name) {
        List<AuthorSummaryDTO> authors = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by author")
    public ResponseEntity<List<BookSummaryDTO>> getAuthorBooks(@PathVariable Long id) {
        List<BookSummaryDTO> books = authorService.getAuthorBooks(id);
        return ResponseEntity.ok(books);
    }
}
