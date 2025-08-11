package com.example.lms.service;

import com.example.lms.repository.BorrowingTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowingTransactionService {

    private final BorrowingTransactionRepository borrowingTransactionRepository;

    public BorrowingTransactionService(BorrowingTransactionRepository borrowingTransactionRepository) {
        this.borrowingTransactionRepository = borrowingTransactionRepository;
    }


}
