package com.example.book_review.respository;

import com.example.book_review.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles,Integer> {
}
