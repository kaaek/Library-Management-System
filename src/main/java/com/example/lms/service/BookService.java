package com.example.lms.service;
import com.example.lms.dto.book.BookRequestDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.book.BookUpdateDTO;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.model.Author;
import com.example.lms.model.Book;
import com.example.lms.model.enums.Category;
import com.example.lms.repository.AuthorRepository;
import com.example.lms.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final String bookNotFoundMsg = "Book not found with ID: ";
    private final String authorNotFoundMsg = "Author not found with ID: ";

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookResponseDTO> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(book -> new BookResponseDTO(
                        book.getId(), book.getTitle(), book.getIsbn(), book.getCategory(), book.getAuthor().getId(), book.isAvailable()
                        )
                )
                .collect(Collectors.toList());
    }

    public BookResponseDTO getBookById(UUID id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(bookNotFoundMsg + id));
        return new BookResponseDTO(
                book.getId(), book.getTitle(), book.getIsbn(), book.getCategory(), book.getAuthor().getId(), book.isAvailable()
        );
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


    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        // Fields:
        String title = bookRequestDTO.getTitle().strip();
        String isbn = bookRequestDTO.getIsbn().strip();
        Category category = bookRequestDTO.getCategory();
        UUID authorId = bookRequestDTO.getAuthorId();

        // Find author.
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + authorId));

        // New book object
        Book newBook = new Book(title, isbn, category, author, true);

        // Update inverse side of the relationship (author)
        author.getBooks().add(newBook);

        // Persist
        bookRepository.save(newBook);

        // Return DTO
        return new BookResponseDTO(
                newBook.getId(), newBook.getTitle(), newBook.getIsbn(), newBook.getCategory(), newBook.getAuthor().getId(), newBook.isAvailable()
        );
    }

    public BookResponseDTO update(UUID bookId, BookUpdateDTO bookUpdateDTO) {
        // Fields:
        String newTitle = bookUpdateDTO.getTitle();
        String newIsbn = bookUpdateDTO.getIsbn();
        Category newCategory = bookUpdateDTO.getCategory();
        UUID newAuthorId = bookUpdateDTO.getAuthorId();
        boolean newAvailable = bookUpdateDTO.isAvailable();

        // Find book.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookNotFoundMsg + bookId));

        // Find author.
        Author author = authorRepository.findById(newAuthorId)
                .orElseThrow(() -> new EntityNotFoundException(authorNotFoundMsg + newAuthorId));

        // Set fields
        book.setTitle(newTitle);
        book.setIsbn(newIsbn);
        book.setCategory(newCategory);
        book.setAvailable(newAvailable);

        // Author:
        Author oldAuthor = book.getAuthor();
        if (!oldAuthor.equals(author)) { // Author changed.
            oldAuthor.getBooks().remove(book);
            author.getBooks().add(book);
            book.setAuthor(author);
        }

        // Persist
        bookRepository.save(book);

        // return DTO
        return new BookResponseDTO(
                book.getId(), book.getTitle(), book.getIsbn(), book.getCategory(), book.getAuthor().getId(), book.isAvailable()
        );
    }

    public void deleteById(UUID bookId){

        // Find book.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookNotFoundMsg + bookId));

        // Remove stale references to the book being deleted.
        Author author = book.getAuthor();
        author.getBooks().remove(book);

        // Flush
        bookRepository.deleteById(bookId);
    }

    public void deleteAll() {
        bookRepository.deleteAll();
    }
}
