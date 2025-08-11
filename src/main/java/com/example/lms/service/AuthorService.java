package com.example.lms.service;

import com.example.lms.dto.author.AuthorRequestDTO;
import com.example.lms.dto.author.AuthorResponseDTO;
import com.example.lms.dto.author.AuthorUpdateDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.response.ApiResponse;
import com.example.lms.exception.AuthorNotFoundException;
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
                        author.getBooks().stream().map(Book::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }

    public AuthorResponseDTO getAuthorById(UUID id){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(authorNotFoundMsg + id));
        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                author.getBooks().stream().map(Book::getId).collect(Collectors.toSet())
        );
    }

    @Transactional
    public List<BookResponseDTO> getBooksByAuthorById(UUID authorId){
        // Fetch author
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorNotFoundMsg + authorId));
        Set<Book> books = author.getBooks();
        return books.stream()
                .map(book -> new BookResponseDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getCategory(),
                        book.getAuthor().getId(),
                        book.isAvailable()
                ))
                .collect(Collectors.toList());

    }

    public ApiResponse<AuthorResponseDTO> createAuthor(AuthorRequestDTO authorRequestDTO){
        // What if said author exists? (Each book has one author)
        // Fields
        String newName = authorRequestDTO.getName();
        String newBio = authorRequestDTO.getBiography();
        Set<UUID> newBookIds = authorRequestDTO.getBookIds();

        List<String> warnings = new ArrayList<>();

        // Find books
        Set<Book> validBooks = new HashSet<>();
        for (UUID bookId: newBookIds){
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                validBooks.add(bookOpt.get());
            } else {
                warnings.add("Book with ID " + bookId + " does not exist and was skipped.");
            }
        }

        // Save author
        Author newAuthor = new Author(
            newName, newBio, validBooks
        );
        authorRepository.save(newAuthor);

        AuthorResponseDTO authorResponseDTO = new AuthorResponseDTO(
              newAuthor.getId(), newAuthor.getName(), newAuthor.getBiography(), newAuthor.getBooks().stream().map(Book::getId).collect(Collectors.toSet())
        );

        return new ApiResponse<>(authorResponseDTO, warnings);
    }

    public ApiResponse<AuthorResponseDTO> update(UUID authorId, AuthorUpdateDTO authorUpdateDTO) {
        // Fields
        String newName = authorUpdateDTO.getName();
        String newBio = authorUpdateDTO.getBiography();
        Set<UUID> newBookIds = authorUpdateDTO.getBookIds();

        // Get Author
        Author oldAuthor = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorNotFoundMsg + authorId));

        // Set fields
        oldAuthor.setName(newName);
        oldAuthor.setBiography(newBio);

        // Validation
        Set<Book> validBooks = new HashSet<>();
        List<String> warnings = new ArrayList<>();

        for (UUID newBookId : newBookIds) {
            bookRepository.findById(newBookId).ifPresentOrElse(
                    validBooks::add,
                    () -> warnings.add("Book with ID " + newBookId + " does not exist and was skipped.")
            );
        }

        // Assign valid books to author
        oldAuthor.setBooks(validBooks);

        // Save changes
        Author updatedAuthor = authorRepository.save(oldAuthor);

        // Build response DTO
        AuthorResponseDTO responseDTO = new AuthorResponseDTO(
                updatedAuthor.getId(),
                updatedAuthor.getName(),
                updatedAuthor.getBiography(),
                updatedAuthor.getBooks().stream()
                        .map(Book::getId)
                        .collect(Collectors.toSet())
        );

        return new ApiResponse<>(responseDTO, warnings);
    }

//    @Transactional
//    public void deleteAuthorById(UUID authorId){
//        // Find author
//        Author author = authorRepository.findById(authorId)
//                .orElseThrow(() -> new AuthorNotFoundException(authorNotFoundMsg + authorId));
//
//
//        // Remove reference
//        Set<Book> books = author.getBooks();
//        for(Book book:books){
//            book.setAuthor(null);
//            bookRepository.save(book);
//        }
//
//        // Flush
//        authorRepository.deleteById(authorId);
//
//    }

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
