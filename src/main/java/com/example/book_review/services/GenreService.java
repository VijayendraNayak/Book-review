package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Book;
import com.example.book_review.models.Genre;
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
public class GenreService {

    @Autowired
    private GenreRepository genreRepo;

    @Autowired
    private ModelMapper modelMapper;

    public GenreResponseDTO createGenre(GenreCreateUpdateDTO dto) {
        // Check if genre with same name already exists
        if (genreRepo.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Genre with this name already exists");
        }

        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());

        Genre saved = genreRepo.save(genre);
        return mapToGenreResponse(saved);
    }

    public GenreResponseDTO updateGenre(int genreId, GenreCreateUpdateDTO dto) {
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        // Check if another genre with same name exists (excluding current genre)
        if (genreRepo.existsByNameIgnoreCaseAndIdNot(dto.getName(), genreId)) {
            throw new IllegalArgumentException("Another genre with this name already exists");
        }

        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());

        Genre updated = genreRepo.save(genre);
        return mapToGenreResponse(updated);
    }

    public void deleteGenre(int genreId) {
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        // Check if genre has associated books
        if (!genre.getBooks().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete genre that has associated books. Remove books first.");
        }

        genreRepo.delete(genre);
    }

    public GenreResponseDTO getGenreById(int genreId) {
        Genre genre = genreRepo.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return mapToGenreResponse(genre);
    }

    public Page<GenreResponseDTO> getAllGenres(Pageable pageable) {
        Page<Genre> genres = genreRepo.findAll(pageable);
        return genres.map(this::mapToGenreResponse);
    }

    public List<GenreSummaryDTO> getAllGenresSummary() {
        List<Genre> genres = genreRepo.findAllByOrderByNameAsc();
        return genres.stream()
                .map(this::mapToGenreSummary)
                .collect(Collectors.toList());
    }

    public Page<GenreResponseDTO> searchGenresByName(String name, Pageable pageable) {
        Page<Genre> genres = genreRepo.findByNameContainingIgnoreCaseOrderByName(name, pageable);
        return genres.map(this::mapToGenreResponse);
    }

    public List<GenreResponseDTO> getPopularGenres(int limit) {
        List<Genre> genres = genreRepo.findGenresOrderByBookCountDesc();
        return genres.stream()
                .limit(limit)
                .map(this::mapToGenreResponse)
                .collect(Collectors.toList());
    }

    public GenreResponseDTO getGenreWithBooks(int genreId) {
        Genre genre = genreRepo.findByIdWithBooks(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return mapToGenreResponseWithBooks(genre);
    }

    private GenreResponseDTO mapToGenreResponse(Genre genre) {
        GenreResponseDTO dto = modelMapper.map(genre, GenreResponseDTO.class);
        dto.setBookCount(genre.getBooks().size());

        // Don't include full book details in basic response to avoid performance issues
        dto.setBooks(null);

        return dto;
    }

    private GenreResponseDTO mapToGenreResponseWithBooks(Genre genre) {
        GenreResponseDTO dto = modelMapper.map(genre, GenreResponseDTO.class);
        dto.setBookCount(genre.getBooks().size());

        List<BookSummaryDTO> bookSummaries = genre.getBooks().stream()
                .map(book -> new BookSummaryDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor()
                ))
                .collect(Collectors.toList());

        dto.setBooks(bookSummaries);
        return dto;
    }

    private GenreSummaryDTO mapToGenreSummary(Genre genre) {
        return new GenreSummaryDTO(genre.getId(), genre.getName());
    }
}