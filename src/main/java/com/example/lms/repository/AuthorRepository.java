package com.example.lms.repository;

import com.example.lms.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    List<Author> findByNameContainingIgnoreCase(String name);
}
