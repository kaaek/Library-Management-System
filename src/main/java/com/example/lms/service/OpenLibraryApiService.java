package com.example.lms.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.lms.dto.openLibrary.BookApiResponseDTO;
import com.example.lms.dto.openLibrary.OpenLibraryResponseDTO;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OpenLibraryApiService {
    
    private final RestTemplate restTemplate;

    public OpenLibraryApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // public String fetchAuthorName(String isbn) {
    // String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
    // try {
    //     OpenLibraryResponseDTO response = restTemplate.getForObject(apiUrl, OpenLibraryResponseDTO.class);

    //     if (response == null || response.getBooks() == null) {
    //         return "Unknown Author"; // API returned nothing or invalid structure
    //     }

    //     BookApiResponseDTO book = response.getBooks().get("ISBN:" + isbn);
    //     if (book == null || book.getAuthors() == null || book.getAuthors().isEmpty()) {
    //         return "Unknown Author"; // no authors found
    //     }

    //     return book.getAuthors().get(0).getName(); // safe to access
    //     } catch (Exception e) {
    //         return "Unknown Author"; // fallback on any request/parse error
    //     }
    // }


    public BookApiResponseDTO fetchBook(String isbn) {
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:"+isbn+"&format=json&jscmd=data";
        try {
            OpenLibraryResponseDTO response = restTemplate.getForObject(apiUrl, OpenLibraryResponseDTO.class);
            if (response == null || response.getBooks() == null) {
            // return null; // API returned nothing or invalid structure
            throw new IllegalArgumentException("Query for ISBN: " + isbn + " returned a null response.");
            }
            BookApiResponseDTO book = response.getBooks().get("ISBN:" + isbn);

            if (book == null) {
                throw new EntityNotFoundException("Open Library returned a non-null response with a null book entry."); // safely handle missing ISBN key
            }
            
            return book;
        
        } catch (Exception e) {
            // return null; // fallback on any request/parse error
            throw new IllegalArgumentException("Query for ISBN: " + isbn + " returned a null response.");
        }
    }

}
