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
@Getter
@Setter
@Table(name="author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Author's name cannot be blank")
    private String name;

    @NotBlank(message = "Author's bio cannot be blank")
    private String bio;

    @NotBlank(message="Author's nationality cannot be blank")
    private String nationality;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books= new HashSet<>();

    public void addBooks(Book book){
        books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBooks(Book book){
        books.remove(book);
        book.getAuthors().remove(this);
    }

    public void clearBooks(){
        for(Book book:new HashSet<>(books)){
            removeBooks(book);
        }
    }
}
