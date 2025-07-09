package com.example.book_review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Table(name="genre")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Genre name cannot be blank")
    private String name;

    @NotBlank(message = "Genre description cannot be blank")
    private String description;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();

    // Convenience methods for Book management
    public void addBook(Book book) {
        books.add(book);
        book.getGenres().add(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.getGenres().remove(this);
    }

    public void clearBooks() {
        for (Book book : new HashSet<>(books)) {
            removeBook(book);
        }
    }

}
