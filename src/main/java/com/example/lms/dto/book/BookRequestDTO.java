package com.example.lms.dto.book;

import com.example.lms.model.enums.Category;
import lombok.Data;

import java.util.UUID;

@Data
public class BookRequestDTO {
    private String title;
    private String isbn;
    private Category category;
    private UUID authorId;
}
