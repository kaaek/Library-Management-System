package com.example.lms.contoller;

import com.example.lms.dto.author.AuthorRequestDTO;
import com.example.lms.dto.author.AuthorResponseDTO;
import com.example.lms.dto.author.AuthorUpdateDTO;
import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.dto.response.ApiResponse;
import com.example.lms.service.AuthorService;
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
    public List<AuthorResponseDTO> getAllAuthors(){
        return authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    public AuthorResponseDTO getAuthorById(@PathVariable UUID id){
        return authorService.getAuthorById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookResponseDTO> getBooksByAuthorById(@PathVariable UUID id){
        return authorService.getBooksByAuthorById(id);
    }

    @PostMapping("/new")
    public AuthorResponseDTO createAuthor(@RequestBody AuthorRequestDTO authorRequestDTO){
        return authorService.createAuthor(authorRequestDTO);
    }

    @PutMapping("/{id}")
    public AuthorResponseDTO updateAuthor(@PathVariable UUID id, @RequestBody AuthorUpdateDTO authorUpdateDTO){
        return authorService.update(id, authorUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteAuthor(@PathVariable UUID id){
        authorService.deleteAuthorById(id);
    }

    @DeleteMapping("/all")
    public void deleteAll(){
        authorService.deleteAllAuthors();
    }
}
