package com.example.book_review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name="rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Min(value=1 ,message = "The minimum rating should be 1")
    @Max(value=5, message = "The maximum rating should be 5")
    private int stars;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        this.createdAt=LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_id", nullable=false)
    private Book book;
}
