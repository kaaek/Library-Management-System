package com.example.lms.dto.borrowings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingTransactionRequestDTO {
    private String isbn;
    private String borrowerEmail;
}
