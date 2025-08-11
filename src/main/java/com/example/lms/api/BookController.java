package com.example.lms.api;

import com.example.lms.dto.book.BookRequestDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.book.BookUpdateDTO;
import com.example.lms.model.enums.Category;
import com.example.lms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public List<BookResponseDTO> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookResponseDTO getBookById(@PathVariable UUID id){
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String author) {

        List<BookResponseDTO> results;

        if (title != null && category != null && author != null) {
            results = bookService.findByTitleAndCategoryAndAuthor(title, category, author);
        } else if (title != null && category != null) {
            results = bookService.findByTitleAndCategory(title, category);
        } else if (title != null && author != null) {
            results = bookService.findByTitleAndAuthor(title, author);
        } else if (category != null && author != null) {
            results = bookService.findByCategoryAndAuthor(category, author);
        } else if (title != null) {
            results = bookService.findByTitle(title);
        } else if (category != null) {
            results = bookService.findByCategory(category);
        } else if (author != null) {
            results = bookService.findByAuthor(author);
        } else {
            results = bookService.getAllBooks();
        }

        return ResponseEntity.ok(results);
    }


    @PostMapping("/new")
    public BookResponseDTO createBook(@RequestBody BookRequestDTO bookRequestDTO){
        return bookService.createBook(bookRequestDTO);
    }

    @PutMapping("/{id}")
    public BookResponseDTO updateBook(@PathVariable UUID id, @RequestBody BookUpdateDTO bookUpdateDTO){
        return bookService.update(id, bookUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable UUID id){
        bookService.deleteById(id);
    }

    @DeleteMapping("/all")
    public void deleteAll(){
        bookService.deleteAll();
    }
}
