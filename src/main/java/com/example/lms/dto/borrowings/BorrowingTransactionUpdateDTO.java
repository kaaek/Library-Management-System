package com.example.lms.dto.borrowings;

import com.example.lms.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingTransactionUpdateDTO {
    private String borrowerEmail;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private TransactionStatus status;
}
