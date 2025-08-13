package com.example.lms.dto.borrowings;

import com.example.lms.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingTransactionResponseDTO {
    private UUID id;
    private UUID bookId;
    private UUID borrowerId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private TransactionStatus status;
}
