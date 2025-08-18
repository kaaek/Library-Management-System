package com.example.lms.dto.borrowings;

import java.time.LocalDate;

import com.example.lms.model.enums.Currency;
import com.example.lms.model.enums.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingTransactionRequestDTO {
    private String isbn;
    private String borrowerEmail;
    private TransactionStatus type; // BORROWED or RETURNED
    private LocalDate returnDate;
    private String cardNumber;
    private Currency currency;
}
