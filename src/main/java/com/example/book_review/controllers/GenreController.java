package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.GenreService;
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
@RequestMapping("/api/genres")
@Tag(name = "Genre Management", description = "APIs for managing genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    @Operation(summary = "Get all genres")
    public ResponseEntity<Page<GenreSummaryDTO>> getAllGenres(Pageable pageable) {
        Page<GenreSummaryDTO> genres = genreService.getAllGenres(pageable);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID")
    public ResponseEntity<GenreResponseDTO> getGenreById(@PathVariable Long id) {
        GenreResponseDTO genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    @Operation(summary = "Create a new genre")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<GenreResponseDTO> createGenre(@Valid @RequestBody GenreCreateUpdateDTO genreDTO) {
        GenreResponseDTO genre = genreService.createGenre(genreDTO);
        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update genre")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<GenreResponseDTO> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreCreateUpdateDTO genreDTO) {
        GenreResponseDTO genre = genreService.updateGenre(id, genreDTO);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search genres by name")
    public ResponseEntity<List<GenreSummaryDTO>> searchGenresByName(@RequestParam String name) {
        List<GenreSummaryDTO> genres = genreService.searchGenresByName(name);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by genre")
    public ResponseEntity<List<BookSummaryDTO>> getGenreBooks(@PathVariable Long id) {
        List<BookSummaryDTO> books = genreService.getGenreBooks(id);
        return ResponseEntity.ok(books);
    }
}
