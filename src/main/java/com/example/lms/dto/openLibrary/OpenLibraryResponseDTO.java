package com.example.lms.dto.openLibrary;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenLibraryResponseDTO {
    private LinkedHashMap<String, BookApiResponseDTO> books; // need this structure since Open Library structures its JSON by keying it by ISBN
}
