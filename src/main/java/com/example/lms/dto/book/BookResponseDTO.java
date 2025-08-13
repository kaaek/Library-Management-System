package com.example.lms.dto.book;
import com.example.lms.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private UUID id;
    private String title;
    private String isbn;
    private Category category;
    private UUID authorId;
    private boolean available;
}
