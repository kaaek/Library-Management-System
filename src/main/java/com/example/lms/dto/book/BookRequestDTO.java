package com.example.lms.dto.book;

import com.example.lms.model.enums.Category;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class BookRequestDTO {
    // private String title;
    private String isbn;
    @Enumerated(EnumType.STRING)
    private Category category;
    // private UUID authorId;
}
