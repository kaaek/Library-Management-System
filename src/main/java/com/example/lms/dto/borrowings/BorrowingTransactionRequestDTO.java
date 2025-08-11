package com.example.lms.dto.borrowings;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BorrowingTransactionRequestDTO {
    private UUID bookId;
    private UUID borrowerId;
}
