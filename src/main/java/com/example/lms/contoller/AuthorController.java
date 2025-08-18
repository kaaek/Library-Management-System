package com.example.lms.contoller;

import com.example.lms.dto.author.AuthorRequestDTO;
import com.example.lms.dto.author.AuthorResponseDTO;
import com.example.lms.dto.author.AuthorUpdateDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.service.AuthorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AuthorResponseDTO>> getAllAuthors(){
        List<AuthorResponseDTO> dtos = authorService.getAllAuthors();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable ("id") UUID id){
        AuthorResponseDTO dto = authorService.getAuthorById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookResponseDTO>> getBooksByAuthorById(@PathVariable ("id") UUID id){
        List<BookResponseDTO> dtos = authorService.getBooksByAuthorById(id);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/new")
    public ResponseEntity<AuthorResponseDTO> createAuthor(@RequestBody AuthorRequestDTO authorRequestDTO){
        AuthorResponseDTO dto = authorService.createAuthor(authorRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable ("id") UUID id, @RequestBody AuthorUpdateDTO authorUpdateDTO){
        AuthorResponseDTO dto = authorService.update(id, authorUpdateDTO);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable ("id") UUID id){
        authorService.deleteAuthorById(id);
        return ResponseEntity.ok("Author with id "+id+"was deleted.");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAll(){
        authorService.deleteAllAuthors();
        return ResponseEntity.ok("Authors were flushed successfully.");
    }
}
