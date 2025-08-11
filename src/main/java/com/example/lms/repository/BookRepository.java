package com.example.lms.repository;

import com.example.lms.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    @Modifying
    @Query("UPDATE Book b SET b.author = null WHERE b.author.id = :authorId")
    void clearAuthorByAuthorId(@Param("authorId") UUID authorId);

    @Modifying
    @Query("UPDATE Book b SET b.author = null")
    void clearAllAuthors();

}
