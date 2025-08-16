package com.example.lms.contoller;

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
    public ResponseEntity<List<BookResponseDTO>> getAllBooks(){
        List<BookResponseDTO> dtos = bookService.getAllBooks();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable ("id") UUID id){
        BookResponseDTO dto = bookService.getBookById(id);
        return ResponseEntity.ok(dto);
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
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequestDTO){
        BookResponseDTO dto = bookService.createBook(bookRequestDTO);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable ("id") UUID id, @RequestBody BookUpdateDTO bookUpdateDTO){
        BookResponseDTO dto = bookService.update(id, bookUpdateDTO);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable ("id") UUID id){
        bookService.deleteById(id);
        return ResponseEntity.ok("Book with id" + id + " was deleted.");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAll(){
        bookService.deleteAll();
        return ResponseEntity.ok("Books were flushed successfully.");
    }
}
