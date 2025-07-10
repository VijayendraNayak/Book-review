package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Author;
import com.example.book_review.models.Book;
import com.example.book_review.repository.AuthorRepository;
import com.example.book_review.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ✅ CREATE AUTHOR
    public AuthorResponseDTO createAuthor(AuthorCreateUpdateDTO dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setBio(dto.getBio());
        author.setNationality(dto.getNationality());

        if (dto.getBookIds() != null && !dto.getBookIds().isEmpty()) {
            List<Book> books = bookRepository.findAllById(dto.getBookIds());
            if (books.size() != dto.getBookIds().size()) {
                throw new EntityNotFoundException("One or more books not found");
            }
            for (Book book : books) {
                author.addBooks(book);
            }
        }

        Author savedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(savedAuthor);
    }

    // 🔍 GET ALL AUTHORS (with pagination) - Return AuthorSummaryDTO as expected
    public Page<AuthorSummaryDTO> getAllAuthors(Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);
        return authors.map(this::mapToAuthorSummary);
    }

    // 🔍 GET ALL AUTHORS (without pagination)
    public List<AuthorResponseDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(this::mapToAuthorResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET ALL AUTHORS SUMMARY (for dropdowns/references)
    public List<AuthorSummaryDTO> getAllAuthorsSummary() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(this::mapToAuthorSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET AUTHOR BY ID - Changed to Long
    public AuthorResponseDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
        return mapToAuthorResponse(author);
    }

    // 🔍 SEARCH AUTHORS BY NAME - Return List<AuthorSummaryDTO>
    public List<AuthorSummaryDTO> searchAuthorsByName(String name) {
        List<Author> authors = authorRepository.findByNameContainingIgnoreCase(name);
        return authors.stream()
                .map(this::mapToAuthorSummary)
                .collect(Collectors.toList());
    }

    // 🔍 GET AUTHORS BY NATIONALITY
    public List<AuthorResponseDTO> getAuthorsByNationality(String nationality) {
        List<Author> authors = authorRepository.findByNationalityIgnoreCase(nationality);
        return authors.stream()
                .map(this::mapToAuthorResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET AUTHOR BOOKS - New method
    public List<BookSummaryDTO> getAuthorBooks(Long id) {
        Author author = authorRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        return author.getBooks().stream()
                .map(book -> {
                    BookSummaryDTO dto = new BookSummaryDTO();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setAuthor(book.getAuthor());
                    dto.setPrice(book.getPrice());
                    dto.setPublishedDate(book.getPublishedDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ✏️ UPDATE AUTHOR - Changed to Long
    public AuthorResponseDTO updateAuthor(Long id, AuthorCreateUpdateDTO dto) {
        Author existingAuthor = authorRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

        existingAuthor.setName(dto.getName());
        existingAuthor.setBio(dto.getBio());
        existingAuthor.setNationality(dto.getNationality());

        if (dto.getBookIds() != null) {
            existingAuthor.clearBooks();
            if (!dto.getBookIds().isEmpty()) {
                List<Book> books = bookRepository.findAllById(dto.getBookIds());
                if (books.size() != dto.getBookIds().size()) {
                    throw new EntityNotFoundException("One or more books not found");
                }
                for (Book book : books) {
                    existingAuthor.addBooks(book);
                }
            }
        }

        Author updatedAuthor = authorRepository.save(existingAuthor);
        return mapToAuthorResponse(updatedAuthor);
    }

    // 🔗 ADD BOOK TO AUTHOR
    public AuthorResponseDTO addBookToAuthor(int authorId, int bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        author.addBooks(book);
        Author savedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(savedAuthor);
    }

    // 🔗 REMOVE BOOK FROM AUTHOR
    public AuthorResponseDTO removeBookFromAuthor(int authorId, int bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        author.removeBooks(book);
        Author savedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(savedAuthor);
    }

    // 🗑️ DELETE AUTHOR - Changed to Long
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

        author.clearBooks();
        authorRepository.delete(author);
    }

    // 🔍 CHECK IF AUTHOR EXISTS
    public boolean existsById(int id) {
        return authorRepository.existsById(id);
    }

    // 📊 GET AUTHOR COUNT
    public long getAuthorCount() {
        return authorRepository.count();
    }

    // 📊 GET AUTHORS WITH MOST BOOKS
    public List<AuthorResponseDTO> getAuthorsWithMostBooks(int limit) {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getBooks().size(), a1.getBooks().size()))
                .limit(limit)
                .map(this::mapToAuthorResponse)
                .collect(Collectors.toList());
    }

    // 🔄 HELPER METHOD: Map Entity to Response DTO
    private AuthorResponseDTO mapToAuthorResponse(Author author) {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBio(author.getBio());
        dto.setNationality(author.getNationality());

        List<BookSummaryDTO> bookSummaries = author.getBooks().stream()
                .map(book -> {
                    BookSummaryDTO bookDto = new BookSummaryDTO();
                    bookDto.setId(book.getId());
                    bookDto.setTitle(book.getTitle());
                    bookDto.setAuthor(book.getAuthor());
                    return bookDto;
                })
                .collect(Collectors.toList());

        dto.setBooks(bookSummaries);
        return dto;
    }

    // 🔄 HELPER METHOD: Map Entity to Summary DTO
    private AuthorSummaryDTO mapToAuthorSummary(Author author) {
        AuthorSummaryDTO dto = new AuthorSummaryDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setNationality(author.getNationality());
        return dto;
    }
}
