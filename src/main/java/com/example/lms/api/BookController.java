package com.example.lms.api;

import com.example.lms.dto.book.BookRequestDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.book.BookUpdateDTO;
import com.example.lms.model.Book;
import com.example.lms.service.BookService;
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
