package com.example.book_review.services;

import com.example.book_review.dto.BookCreateUpdateDTO;
import com.example.book_review.dto.BookResponseDTO;
import com.example.book_review.dto.BookSummaryDTO;
import com.example.book_review.dto.AuthorSummaryDTO;
import com.example.book_review.dto.GenreSummaryDTO;
import com.example.book_review.models.Book;
import com.example.book_review.models.Author;
import com.example.book_review.models.Genre;
import com.example.book_review.models.Rating;
import com.example.book_review.repository.BookRepository;
import com.example.book_review.repository.AuthorRepository;
import com.example.book_review.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ✅ CREATE BOOK
    public BookResponseDTO createBook(BookCreateUpdateDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setPublishedDate(dto.getPublishedDate());

        // Link genres if provided
        if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(dto.getGenreIds());
            if (genres.size() != dto.getGenreIds().size()) {
                throw new EntityNotFoundException("One or more genres not found");
            }

            for (Genre genre : genres) {
                book.addGenre(genre);
            }
        }

        // Link authors if provided
        if (dto.getAuthorIds() != null && !dto.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllById(dto.getAuthorIds());
            if (authors.size() != dto.getAuthorIds().size()) {
                throw new EntityNotFoundException("One or more authors not found");
            }

            for (Author author : authors) {
                book.addAuthors(author);
            }
        }

        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    // 🔍 GET ALL BOOKS (with pagination)
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::mapToBookResponse);
    }

    // 🔍 GET ALL BOOKS (without pagination)
    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET ALL BOOKS SUMMARY (for dropdowns/references)
    public List<BookSummaryDTO> getAllBooksSummary() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::mapToBookSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOK BY ID
    public BookResponseDTO getBookById(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }

    // 🔍 SEARCH BOOKS BY TITLE
    public List<BookResponseDTO> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOKS BY PRICE RANGE
    public List<BookResponseDTO> getBooksByPriceRange(int minPrice, int maxPrice) {
        List<Book> books = bookRepository.findByPriceBetween(minPrice, maxPrice);
        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // ✏️ UPDATE BOOK
    public BookResponseDTO updateBook(int id, BookCreateUpdateDTO dto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(dto.getTitle());
        existingBook.setDescription(dto.getDescription());
        existingBook.setPrice(dto.getPrice());
        existingBook.setPublishedDate(dto.getPublishedDate());

        // Update genre associations if provided
        if (dto.getGenreIds() != null) {
            // Clear existing genres
            existingBook.clearGenres();

            // Add new genres
            if (!dto.getGenreIds().isEmpty()) {
                List<Genre> genres = genreRepository.findAllById(dto.getGenreIds());
                if (genres.size() != dto.getGenreIds().size()) {
                    throw new EntityNotFoundException("One or more genres not found");
                }

                for (Genre genre : genres) {
                    existingBook.addGenre(genre);
                }
            }
        }

        // Update author associations if provided
        if (dto.getAuthorIds() != null) {
            // Clear existing authors
            existingBook.clearAuthors();

            // Add new authors
            if (!dto.getAuthorIds().isEmpty()) {
                List<Author> authors = authorRepository.findAllById(dto.getAuthorIds());
                if (authors.size() != dto.getAuthorIds().size()) {
                    throw new EntityNotFoundException("One or more authors not found");
                }

                for (Author author : authors) {
                    existingBook.addAuthors(author);
                }
            }
        }

        Book updatedBook = bookRepository.save(existingBook);
        return mapToBookResponse(updatedBook);
    }

    // 🔗 ADD AUTHOR TO BOOK
    public BookResponseDTO addAuthorToBook(int bookId, int authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

        book.addAuthors(author);
        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    // 🔗 REMOVE AUTHOR FROM BOOK
    public BookResponseDTO removeAuthorFromBook(int bookId, int authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

        book.removeAuthors(author);
        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    // 🔗 ADD GENRE TO BOOK
    public BookResponseDTO addGenreToBook(int bookId, int genreId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));

        book.addGenre(genre);
        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    // 🔗 REMOVE GENRE FROM BOOK
    public BookResponseDTO removeGenreFromBook(int bookId, int genreId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));

        book.removeGenre(genre);
        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    // 🗑️ DELETE BOOK
    public void deleteBook(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        // Clear all associations before deletion
        book.clearAuthors();
        book.clearGenres();

        bookRepository.delete(book);
    }

    // 🔍 CHECK IF BOOK EXISTS
    public boolean existsById(int id) {
        return bookRepository.existsById(id);
    }

    // 📊 GET BOOK COUNT
    public long getBookCount() {
        return bookRepository.count();
    }

    // 📊 GET HIGHEST RATED BOOKS
    public List<BookResponseDTO> getHighestRatedBooks(int limit) {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .sorted((b1, b2) -> Double.compare(calculateAverageRating(b2), calculateAverageRating(b1)))
                .limit(limit)
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // 🔄 HELPER METHOD: Calculate average rating
    private double calculateAverageRating(Book book) {
        if (book.getRatings().isEmpty()) {
            return 0.0;
        }
        return book.getRatings().stream()
                .mapToDouble(Rating::getStars)  // Changed from getRating to getStars
                .average()
                .orElse(0.0);
    }

    // 🔄 HELPER METHOD: Map Entity to Response DTO
    private BookResponseDTO mapToBookResponse(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setPublishedDate(book.getPublishedDate());

        // Map genres to GenreSummaryDTO
        List<GenreSummaryDTO> genreSummaries = book.getGenres().stream()
                .map(genre -> new GenreSummaryDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
        dto.setGenres(genreSummaries);

        // Map authors to AuthorSummaryDTO
        List<AuthorSummaryDTO> authorSummaries = book.getAuthors().stream()
                .map(author -> new AuthorSummaryDTO(author.getId(), author.getName(), author.getNationality()))
                .collect(Collectors.toList());
        dto.setAuthors(authorSummaries);

        // Calculate average rating and review count
        dto.setAverageRating(calculateAverageRating(book));
        dto.setTotalReviews(book.getReviews().size());

        return dto;
    }

    // 🔄 HELPER METHOD: Map Entity to Summary DTO
    private BookSummaryDTO mapToBookSummary(Book book) {
        BookSummaryDTO dto = new BookSummaryDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor()); // This now works with the helper method
        dto.setPrice(book.getPrice());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setAverageRating(calculateAverageRating(book));
        return dto;
    }
}
