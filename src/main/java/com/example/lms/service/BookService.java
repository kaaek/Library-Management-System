package com.example.lms.service;
import com.example.lms.dto.book.BookRequestDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.book.BookUpdateDTO;
import com.example.lms.dto.openLibrary.AuthorApiResponseDTO;
import com.example.lms.dto.openLibrary.BookApiResponseDTO;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.model.Author;
import com.example.lms.model.Book;
import com.example.lms.model.BorrowingTransaction;
import com.example.lms.model.enums.Category;
import com.example.lms.repository.AuthorRepository;
import com.example.lms.repository.BookRepository;
import com.example.lms.repository.BorrowingTransactionRepository;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
// import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final String bookNotFoundMsg = "Book not found with ID: ";
//     private final String authorNotFoundMsg = "Author not found with ID: ";

    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BorrowingTransactionRepository borrowingTransactionRepository;
    private final OpenLibraryApiService openLibraryApiService;

    public BookService(ModelMapper modelMapper, BookRepository bookRepository, AuthorRepository authorRepository, BorrowingTransactionRepository borrowingTransactionRepository, OpenLibraryApiService openLibraryApiService) {
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.borrowingTransactionRepository = borrowingTransactionRepository;
        this.openLibraryApiService = openLibraryApiService;
    }

    public List<BookResponseDTO> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(book -> new BookResponseDTO(
                        book.getId(), book.getTitle(), book.getIsbn(), book.getCategory(), book.getAuthor().getId(), book.isAvailable()
                        )
                )
                .toList();
    }

    public BookResponseDTO getBookById(UUID id){
        Book book = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(bookNotFoundMsg + id));
        return modelMapper.map(book, BookResponseDTO.class);
    }

    public List<BookResponseDTO> findByTitleAndCategoryAndAuthor(String title, Category category, String authorName) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseAndCategoryAndAuthor_NameContainingIgnoreCase(title, category, authorName);
        return books.stream()
                .map(book -> new BookResponseDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getCategory(),
                        book.getAuthor().getId(),
                        book.isAvailable()))
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> findByTitleAndCategory(String title, Category category) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseAndCategory(title, category);
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

    public List<BookResponseDTO> findByTitleAndAuthor(String title, String authorName) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseAndAuthor_NameContainingIgnoreCase(title, authorName);
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

    public List<BookResponseDTO> findByCategoryAndAuthor(Category category, String authorName){
        List<Book> books = bookRepository.findByCategoryAndAuthor_NameContainingIgnoreCase(category, authorName);
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

    public List<BookResponseDTO> findByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
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

    public List<BookResponseDTO> findByCategory (Category category) {
        List<Book> books = bookRepository.findByCategory(category);
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

    public List<BookResponseDTO> findByAuthor(String authorName) {
        List<Book> books = bookRepository.findByAuthor_NameContainingIgnoreCase(authorName);
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

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        
        // 1. Extract fields from the DTO
        String isbn = bookRequestDTO.getIsbn().strip();

        // Check if ISBN exists in db
        if(bookRepository.existsByIsbn(isbn)){
                throw new IllegalArgumentException("Book already exists with ISBN: "+isbn);
        }

        Category category = bookRequestDTO.getCategory();

        // 2. Fetch book from Open Library
        BookApiResponseDTO responseDTO = openLibraryApiService.fetchBook(isbn);

        // 3. Extract fields from Open Library's response
        // Title
        String title = responseDTO.getTitle();

        // Author: name
        List<AuthorApiResponseDTO> authors = responseDTO.getAuthors();
        String authorName = (authors != null && !authors.isEmpty()) 
                        ? authors.get(0).getName() 
                        : "Unknown Author";
        
        // Author: find in DB.
        List<Author> query = authorRepository.findByNameContainingIgnoreCase(authorName);
        Author author;
        if(!query.isEmpty()) {
                author = query.get(0);
        } else {
                author = authorRepository.save(new Author(authorName, "")); // TODO: Retreive the biography at a later point.
        }

        // New book object
        Book newBook = new Book(title, isbn, category, author, true);

        // Persist
        bookRepository.save(newBook);

        // Return DTO
        return modelMapper.map(newBook, BookResponseDTO.class);
    }

    public BookResponseDTO update(UUID bookId, BookUpdateDTO bookUpdateDTO) {
        // Fields:
        // String newTitle = bookUpdateDTO.getTitle().strip();
        // String newIsbn = bookUpdateDTO.getIsbn().strip();
        // String newIsbn = Optional.ofNullable(bookUpdateDTO.getIsbn()).orElse("").strip();
        Category newCategory = bookUpdateDTO.getCategory();
        // UUID newAuthorId = bookUpdateDTO.getAuthorId();
        boolean newAvailable = bookUpdateDTO.isAvailable();

        // Find book.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(bookNotFoundMsg + bookId));

        // Find author.
        // Author author = authorRepository.findById(newAuthorId)
        //         .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + newAuthorId));

        // Set fields
        // book.setTitle(newTitle);

        // ISBNs are different:
        // if(!(book.getIsbn().equalsIgnoreCase(newIsbn)) && bookRepository.existsByIsbn(newIsbn)){
                // throw new IllegalArgumentException("Book already exists with ISBN: " + newIsbn + ". Provide a unique ISBN.");
        // }

        // book.setIsbn(newIsbn);
        book.setCategory(newCategory);
        book.setAvailable(newAvailable);
        // book.setAuthor(author);

        // Persist
        bookRepository.save(book);

        // return DTO
        return modelMapper.map(book, BookResponseDTO.class);
    }

    public void deleteById(UUID bookId){

        // Find book.
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException(bookNotFoundMsg + bookId));

        // There's no need keeping the transactions tied to this book anymore, cascade delete.
        List<BorrowingTransaction> queries = borrowingTransactionRepository.findByBook(book);
        for (BorrowingTransaction transaction : queries) {
                borrowingTransactionRepository.delete(transaction);
        }

        // Flush
        bookRepository.delete(book);
    }

    public void deleteAll() {
        borrowingTransactionRepository.deleteAll(); // Cascade delete
        bookRepository.deleteAll();
    }
}
