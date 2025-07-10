package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.BookService;
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
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "APIs for managing books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public ResponseEntity<Page<BookSummaryDTO>> getAllBooks(Pageable pageable) {
        Page<BookSummaryDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    @Operation(summary = "Create a new book")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateUpdateDTO bookDTO) {
        BookResponseDTO book = bookService.createBook(bookDTO);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookCreateUpdateDTO bookDTO) {
        BookResponseDTO book = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title")
    public ResponseEntity<List<BookSummaryDTO>> searchBooksByTitle(@RequestParam String title) {
        List<BookSummaryDTO> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author")
    public ResponseEntity<List<BookSummaryDTO>> getBooksByAuthor(@PathVariable Long authorId) {
        List<BookSummaryDTO> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/genre/{genreId}")
    @Operation(summary = "Get books by genre")
    public ResponseEntity<List<BookSummaryDTO>> getBooksByGenre(@PathVariable Long genreId) {
        List<BookSummaryDTO> books = bookService.getBooksByGenre(genreId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get reviews for a book")
    public ResponseEntity<List<ReviewSummaryDTO>> getBookReviews(@PathVariable Long id) {
        List<ReviewSummaryDTO> reviews = bookService.getBookReviews(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}/ratings")
    @Operation(summary = "Get ratings for a book")
    public ResponseEntity<List<RatingSummaryDTO>> getBookRatings(@PathVariable Long id) {
        List<RatingSummaryDTO> ratings = bookService.getBookRatings(id);
        return ResponseEntity.ok(ratings);
    }


}
