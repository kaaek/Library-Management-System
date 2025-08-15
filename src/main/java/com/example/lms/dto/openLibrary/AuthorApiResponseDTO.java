package com.example.lms.dto.openLibrary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorApiResponseDTO {
    private String url; // optional, may store the Open Library URL
    private String name;
}

