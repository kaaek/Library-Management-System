package com.example.lms.repository;

import com.example.lms.model.Book;
import com.example.lms.model.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    @Modifying
    @Query("UPDATE Book b SET b.author = null WHERE b.author.id = :authorId")
    void clearAuthorByAuthorId(@Param("authorId") UUID authorId);

    @Modifying
    @Query("UPDATE Book b SET b.author = null")
    void clearAllAuthors();

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByCategory(Category category);

    List<Book> findByAuthor_NameContainingIgnoreCase(String authorName);

    List<Book> findByTitleContainingIgnoreCaseAndCategoryAndAuthor_NameContainingIgnoreCase(String title, Category category, String authorName);

    List<Book> findByTitleContainingIgnoreCaseAndCategory(String title, Category category);

    List<Book> findByTitleContainingIgnoreCaseAndAuthor_NameContainingIgnoreCase(String title, String author);

    List <Book> findByCategoryAndAuthor_NameContainingIgnoreCase(Category category, String authorName);

}
