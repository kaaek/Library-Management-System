package com.example.lms.dto.borrowings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingTransactionRequestDTO {
    private UUID bookId;
    private UUID borrowerId;
}
