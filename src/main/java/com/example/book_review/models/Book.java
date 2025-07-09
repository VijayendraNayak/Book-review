package com.example.book_review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Positive(message = "Price of the book cannot be blank")
    private int price;

    @PastOrPresent(message = "Dates should be past or present")
    private LocalDate publishedDate;

    @OneToMany(mappedBy="book",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Reviews> reviews=new ArrayList<>();

    public void addReview(Reviews review){
        reviews.add(review);
        review.setBook(this);
    }
    public void removeReview(Reviews review){
        reviews.remove(review);
        review.setBook(null);
    }

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Rating> ratings=new ArrayList<>();

    public void addRating(Rating rating){
        ratings.add(rating);
        rating.setBook(this);
    }

    public void removeRating(Rating rating){
        ratings.remove(rating);
        rating.setBook(null);
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name="book_id"),
            inverseJoinColumns=@JoinColumn(name="genre_id")
    )
    private Set<Genre> genres=new HashSet<>();

    public void addGenre(Genre genre){
        genres.add(genre);
        genre.getBooks().add(this);
    }
    public void removeGenre(Genre genre){
        genres.remove(genre);
        genre.getBooks().remove(this);
    }
    public void clearGenres() {
        for (Genre genre : new HashSet<>(genres)) {
            removeGenre(genre);
        }
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinTable(
            name="book_author",
            joinColumns = @JoinColumn(name="book_id"),
            inverseJoinColumns = @JoinColumn(name="author_id")
    )
    private Set<Author> authors=new HashSet<>();

    public void addAuthors(Author author){
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthors(Author author){
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void clearAuthors(){
        for(Author author:new HashSet<>(authors)){
            removeAuthors(author);
        }
    }
}
