package com.example.lms.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.lms.dto.openLibrary.BookApiResponseDTO;
import com.example.lms.dto.openLibrary.OpenLibraryResponseDTO;

@Service
public class OpenLibraryApiService {
    
    private final RestTemplate restTemplate;

    public OpenLibraryApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchAuthorName(String isbn) {
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:"+isbn+"&format=json&jscmd=data";
        try {
            OpenLibraryResponseDTO response = restTemplate.getForObject(apiUrl, OpenLibraryResponseDTO.class);
            BookApiResponseDTO book = response.getBooks().get("ISBN:" + isbn);
            String authorName = book != null && !book.getAuthors().isEmpty() ? book.getAuthors().get(0).getName() : "Unknown Author";
            return authorName;
        } catch (Exception e) {
            return "Unknown Author";
        }
    }

    public BookApiResponseDTO fetchBook(String isbn) {
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:"+isbn+"&format=json&jscmd=data";
        try {
            OpenLibraryResponseDTO response = restTemplate.getForObject(apiUrl, OpenLibraryResponseDTO.class);
            BookApiResponseDTO book = response.getBooks().get("ISBN:" + isbn);
            return book;
        } catch (Exception e) {
            return null;
        }
    }

}
