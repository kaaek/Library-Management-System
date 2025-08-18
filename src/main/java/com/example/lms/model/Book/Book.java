package com.example.lms.model.Book;
import com.example.lms.model.Author;
import com.example.lms.model.enums.Category;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @Column(name = "available", nullable = false)
    private boolean available;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "properties", columnDefinition = "jsonb")
    private Properties properties;

    public Book(String title, String isbn, Category category, Author author, boolean available, Properties properties) {
        this.title = title;
        this.isbn = isbn;
        this.category = category;
        this.author = author;
        this.available = available;
        this.properties = properties;
    }

    /*
     * public Book(String title, String isbn, Category category, Author author, boolean available) {
        this.title = title;
        this.isbn = isbn;
        this.category = category;
        this.author = author;
        this.available = available;
    }
     */
}
