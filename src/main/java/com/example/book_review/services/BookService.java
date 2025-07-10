package com.example.book_review.services;

import com.example.book_review.dto.*;
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

    // 🔍 GET ALL BOOKS (with pagination) - Return BookSummaryDTO as expected by controller
    public Page<BookSummaryDTO> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::mapToBookSummary);
    }

    // 🔍 GET ALL BOOKS (without pagination)
    // public List<BookResponseDTO> getAllBooks() {
    //     List<Book> books = bookRepository.findAll();
    //     return books.stream()
    //             .map(this::mapToBookResponse)
    //             .collect(Collectors.toList());
    // }

    // 🔍 GET ALL BOOKS SUMMARY (for dropdowns/references)
    // public List<BookSummaryDTO> getAllBooksSummary() {
    //     List<Book> books = bookRepository.findAll();
    //     return books.stream()
    //             .map(this::mapToBookSummary)
    //             .collect(Collectors.toList());
    // }

    // 🔍 GET BOOK BY ID - Changed to Long
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }

    // 🔍 SEARCH BOOKS BY TITLE - Return List<BookSummaryDTO>
    public List<BookSummaryDTO> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(this::mapToBookSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOKS BY AUTHOR - New method
    public List<BookSummaryDTO> getBooksByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        return author.getBooks().stream()
                .map(this::mapToBookSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOKS BY GENRE - New method
    public List<BookSummaryDTO> getBooksByGenre(Long genreId) {
        Genre genre = genreRepository.findById(genreId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return genre.getBooks().stream()
                .map(this::mapToBookSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOK REVIEWS - New method
    public List<ReviewSummaryDTO> getBookReviews(Long bookId) {
        Book book = bookRepository.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return book.getReviews().stream()
                .map(review -> {
                    ReviewSummaryDTO dto = new ReviewSummaryDTO();
                    dto.setId(review.getId());
                    dto.setComment(review.getComment());
                    dto.setCreatedAt(review.getCreatedAt());
                    dto.setUsername(review.getUser().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOK RATINGS - New method
    public List<RatingSummaryDTO> getBookRatings(Long bookId) {
        Book book = bookRepository.findById(bookId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return book.getRatings().stream()
                .map(rating -> {
                    RatingSummaryDTO dto = new RatingSummaryDTO();
                    dto.setId(rating.getId());
                    dto.setStars(rating.getStars());
                    dto.setCreatedAt(rating.getCreatedAt());
                    dto.setUsername(rating.getUser().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 🔍 GET BOOKS BY PRICE RANGE
    // public List<BookResponseDTO> getBooksByPriceRange(int minPrice, int maxPrice) {
    //     List<Book> books = bookRepository.findByPriceBetween(minPrice, maxPrice);
    //     return books.stream()
    //             .map(this::mapToBookResponse)
    //             .collect(Collectors.toList());
    // }

    // ✏️ UPDATE BOOK - Changed to Long
    public BookResponseDTO updateBook(Long id, BookCreateUpdateDTO dto) {
        Book existingBook = bookRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(dto.getTitle());
        existingBook.setDescription(dto.getDescription());
        existingBook.setPrice(dto.getPrice());
        existingBook.setPublishedDate(dto.getPublishedDate());

        // Update genre associations if provided
        if (dto.getGenreIds() != null) {
            existingBook.clearGenres();
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
            existingBook.clearAuthors();
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
    // public BookResponseDTO addAuthorToBook(int bookId, int authorId) {
    //     Book book = bookRepository.findById(bookId)
    //             .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    //     Author author = authorRepository.findById(authorId)
    //             .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

    //     book.addAuthors(author);
    //     Book savedBook = bookRepository.save(book);
    //     return mapToBookResponse(savedBook);
    // }

    // 🔗 REMOVE AUTHOR FROM BOOK
    // public BookResponseDTO removeAuthorFromBook(int bookId, int authorId) {
    //     Book book = bookRepository.findById(bookId)
    //             .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    //     Author author = authorRepository.findById(authorId)
    //             .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

    //     book.removeAuthors(author);
    //     Book savedBook = bookRepository.save(book);
    //     return mapToBookResponse(savedBook);
    // }

    // 🔗 ADD GENRE TO BOOK
    // public BookResponseDTO addGenreToBook(int bookId, int genreId) {
    //     Book book = bookRepository.findById(bookId)
    //             .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    //     Genre genre = genreRepository.findById(genreId)
    //             .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));

    //     book.addGenre(genre);
    //     Book savedBook = bookRepository.save(book);
    //     return mapToBookResponse(savedBook);
    // }

    // 🔗 REMOVE GENRE FROM BOOK
    // public BookResponseDTO removeGenreFromBook(int bookId, int genreId) {
    //     Book book = bookRepository.findById(bookId)
    //             .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    //     Genre genre = genreRepository.findById(genreId)
    //             .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));

    //     book.removeGenre(genre);
    //     Book savedBook = bookRepository.save(book);
    //     return mapToBookResponse(savedBook);
    // }

    // 🗑️ DELETE BOOK - Changed to Long
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        book.clearAuthors();
        book.clearGenres();
        bookRepository.delete(book);
    }

    // 🔍 CHECK IF BOOK EXISTS
    // public boolean existsById(int id) {
    //     return bookRepository.existsById(id);
    // }

    // 📊 GET BOOK COUNT
    // public long getBookCount() {
    //     return bookRepository.count();
    // }

    // 📊 GET HIGHEST RATED BOOKS
    // public List<BookResponseDTO> getHighestRatedBooks(int limit) {
    //     List<Book> books = bookRepository.findAll();
    //     return books.stream()
    //             .sorted((b1, b2) -> Double.compare(calculateAverageRating(b2), calculateAverageRating(b1)))
    //             .limit(limit)
    //             .map(this::mapToBookResponse)
    //             .collect(Collectors.toList());
    // }

    // 🔄 HELPER METHOD: Calculate average rating
    private double calculateAverageRating(Book book) {
        if (book.getRatings().isEmpty()) {
            return 0.0;
        }
        return book.getRatings().stream()
                .mapToDouble(Rating::getStars)
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

        List<GenreSummaryDTO> genreSummaries = book.getGenres().stream()
                .map(genre -> new GenreSummaryDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
        dto.setGenres(genreSummaries);

        List<AuthorSummaryDTO> authorSummaries = book.getAuthors().stream()
                .map(author -> new AuthorSummaryDTO(author.getId(), author.getName(), author.getNationality()))
                .collect(Collectors.toList());
        dto.setAuthors(authorSummaries);

        dto.setAverageRating(calculateAverageRating(book));
        dto.setTotalReviews(book.getReviews().size());

        return dto;
    }

    // 🔄 HELPER METHOD: Map Entity to Summary DTO
    private BookSummaryDTO mapToBookSummary(Book book) {
        BookSummaryDTO dto = new BookSummaryDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setAverageRating(calculateAverageRating(book));
        return dto;
    }
}
