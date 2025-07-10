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
        if (genreRepo.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Genre with this name already exists");
        }

        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());

        Genre saved = genreRepo.save(genre);
        return mapToGenreResponse(saved);
    }

    public GenreResponseDTO updateGenre(Long genreId, GenreCreateUpdateDTO dto) {
        Genre genre = genreRepo.findById(genreId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        if (genreRepo.existsByNameIgnoreCaseAndIdNot(dto.getName(), genreId.intValue())) {
            throw new IllegalArgumentException("Another genre with this name already exists");
        }

        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());

        Genre updated = genreRepo.save(genre);
        return mapToGenreResponse(updated);
    }

    public void deleteGenre(Long genreId) {
        Genre genre = genreRepo.findById(genreId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        if (!genre.getBooks().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete genre that has associated books. Remove books first.");
        }

        genreRepo.delete(genre);
    }

    public GenreResponseDTO getGenreById(Long genreId) {
        Genre genre = genreRepo.findById(genreId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return mapToGenreResponse(genre);
    }

    public Page<GenreSummaryDTO> getAllGenres(Pageable pageable) {
        Page<Genre> genres = genreRepo.findAll(pageable);
        return genres.map(this::mapToGenreSummary);
    }

    public List<GenreSummaryDTO> searchGenresByName(String name) {
        List<Genre> genres = genreRepo.findByNameContainingIgnoreCaseOrderByName(name);
        return genres.stream()
                .map(this::mapToGenreSummary)
                .collect(Collectors.toList());
    }

    public List<BookSummaryDTO> getGenreBooks(Long id) {
        Genre genre = genreRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return genre.getBooks().stream()
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

    private GenreResponseDTO mapToGenreResponse(Genre genre) {
        GenreResponseDTO dto = modelMapper.map(genre, GenreResponseDTO.class);
        dto.setBookCount(genre.getBooks().size());
        dto.setBooks(null); // Don't include full book details in basic response
        return dto;
    }

    private GenreSummaryDTO mapToGenreSummary(Genre genre) {
        return new GenreSummaryDTO(genre.getId(), genre.getName());
    }
}
