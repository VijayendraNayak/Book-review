package com.example.book_review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="review")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime createdAt;

    @NotBlank(message = "Comment cannot be blank")
    private String comment;

    @PrePersist
    public void onCreate(){
        this.createdAt=LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable=false)
    private Book book;
}
