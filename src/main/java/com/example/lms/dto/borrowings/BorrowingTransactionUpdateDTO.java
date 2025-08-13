package com.example.lms.dto.borrowings;

import com.example.lms.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BorrowingTransactionUpdateDTO {
    private UUID id;
    private UUID borrowerId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private TransactionStatus status;
}
