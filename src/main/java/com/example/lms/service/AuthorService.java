package com.example.lms.service;

import com.example.lms.dto.author.AuthorRequestDTO;
import com.example.lms.dto.author.AuthorResponseDTO;
import com.example.lms.dto.author.AuthorUpdateDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.model.Author;
import com.example.lms.model.Book;
import com.example.lms.repository.AuthorRepository;
import com.example.lms.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private String authorNotFoundMsg = "Author not found with ID: ";

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<AuthorResponseDTO> getAllAuthors(){
        return authorRepository.findAll()
                .stream()
                .map(author -> new AuthorResponseDTO(
                        author.getId(),
                        author.getName(),
                        author.getBiography(),
                        bookRepository.findByAuthor_NameContainingIgnoreCase(author.getName())
                                .stream()
                                .map(Book::getId)
                                .toList()
                ))
                .collect(Collectors.toList());
    }

    public AuthorResponseDTO getAuthorById(UUID id){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + id));
        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                bookRepository.findByAuthor_NameContainingIgnoreCase(author.getName())
                        .stream()
                        .map(Book::getId)
                        .toList()
        );
    }

    @Transactional
    public List<BookResponseDTO> getBooksByAuthorById(UUID authorId){
        // Fetch author
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + authorId));
        List<Book> books = bookRepository.findByAuthor_NameContainingIgnoreCase(author.getName());
        return books.stream()
                .map(book -> new BookResponseDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getCategory(),
                        book.getAuthor().getId(),
                        book.isAvailable()
                ))
                .toList();

    }

    public AuthorResponseDTO createAuthor(AuthorRequestDTO authorRequestDTO){

        // Fields
        String newName = authorRequestDTO.getName().strip();
        String newBio = authorRequestDTO.getBiography().strip();

        // Check if author already exists
        if(!authorRepository.findByNameContainingIgnoreCase(newName).isEmpty()) {
            throw new IllegalArgumentException("Author already exists");
        }

        Author newAuthor = new Author(newName, newBio);
        authorRepository.save(newAuthor);

        return new AuthorResponseDTO(
                newAuthor.getId(),
                newAuthor.getName(),
                newAuthor.getBiography(),
                bookRepository.findByAuthor_NameContainingIgnoreCase(newAuthor.getName())
                        .stream()
                        .map(Book::getId)
                        .toList()
        );
    }

    public AuthorResponseDTO update(UUID authorId, AuthorUpdateDTO authorUpdateDTO) {
        // Fields
        String newName = authorUpdateDTO.getName();
        String newBio = authorUpdateDTO.getBiography();

        // Get Author
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + authorId));

        // Set fields
        author.setName(newName);
        author.setBiography(newBio);

        // Save changes
        authorRepository.save(author);

        // Build response DTO

        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                bookRepository.findByAuthor_NameContainingIgnoreCase(author.getName())
                        .stream()
                        .map(Book::getId)
                        .toList()
        );
    }

    @Transactional
    public void deleteAuthorById(UUID authorId){
        bookRepository.clearAuthorByAuthorId(authorId);
        authorRepository.deleteById(authorId);
    }


    @Transactional
    public void deleteAllAuthors(){
        // Bulk clear author references from all books
        bookRepository.clearAllAuthors();

        // Delete all authors
        authorRepository.deleteAll();
    }

}
