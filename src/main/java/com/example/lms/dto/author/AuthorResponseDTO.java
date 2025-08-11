package com.example.lms.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthorResponseDTO {
    private UUID id;
    private String name;
    private String biography;
    private Set<UUID> bookIds;
}
