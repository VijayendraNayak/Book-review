package com.example.book_review.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message="Invalid email format")
    private String email;

    @Column(unique = true)
    @NotBlank(message="username cannot be blank")
    private String username;

    @Size(min=6, message="The password should have atleast 6 characters")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="role_id",nullable = false)
    private Roles role;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Reviews> reviews=new ArrayList<>();

    public void addReview(Reviews review){
        reviews.add(review);
        review.setUser(this);
    }
    public void removeReview(Reviews review){
        reviews.remove(review);
        review.setUser(null);
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Rating> ratings=new ArrayList<>();

    public void addRating(Rating rating){
        ratings.add(rating);
        rating.setUser(this);
    }

    public void removeRating(Rating rating){
        ratings.remove(rating);
        rating.setUser(null);
    }

}
