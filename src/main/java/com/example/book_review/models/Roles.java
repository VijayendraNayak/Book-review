package com.example.book_review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="role")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "description of the role cannot be blank")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "role")
    private List<User> users=new ArrayList<>();

    public void addUser(User user){
        users.add(user);
        user.setRole(this);
    }

    public void removeUser(User user){
        users.remove(user);
        user.setRole(null);
    }
}
