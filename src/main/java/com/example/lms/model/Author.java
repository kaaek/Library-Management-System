package com.example.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue
    @Column(name = "id",updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "biography", nullable = false)
    private String biography;
    
//    @OneToMany(mappedBy = "author", cascade = CascadeType.PERSIST) //, orphanRemoval = true) // do not need to set orphanRemoval = true unless you want books to be deleted when removed from author's collection.
//    private Set<Book> books;

    public Author(String name, String biography){
        this.name = name;
        this.biography = biography;
    }
}
