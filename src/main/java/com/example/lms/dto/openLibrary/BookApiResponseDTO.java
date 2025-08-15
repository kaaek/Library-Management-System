package com.example.lms.dto.openLibrary;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookApiResponseDTO {
    private String title;
    private List<Author> authors;

    @Data
    public static class Author {
        private String name;
    }
    
}
