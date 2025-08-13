package com.example.lms.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorUpdateDTO {
    private String name;
    private String biography;
    //private Set<UUID> bookIds;
}
