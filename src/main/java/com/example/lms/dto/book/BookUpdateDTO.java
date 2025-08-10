package com.example.lms.dto.book;
import com.example.lms.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BookUpdateDTO {
    private String title;
    private String isbn;
    private Category category;
    private UUID authorId;
    private boolean available;
}
