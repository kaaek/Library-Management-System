package com.example.lms.dto.openLibrary;

import java.util.Map;

import lombok.Data;

@Data
public class OpenLibraryResponseDTO {
    private Map<String, BookApiResponseDTO> books; // need this structure since Open Library structures its JSON by keying it by ISBN
}
