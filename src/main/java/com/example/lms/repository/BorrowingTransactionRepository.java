package com.example.lms.repository;

import com.example.lms.model.BorrowingTransaction;
import com.example.lms.model.Book.Book;
import com.example.lms.model.enums.TransactionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.lms.model.Borrower;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingTransactionRepository extends JpaRepository<BorrowingTransaction, UUID> {
    List<BorrowingTransaction> findByBorrower(Borrower borrower);
    List<BorrowingTransaction> findByBook(Book book);
    void deleteByBorrower(Borrower borrower);
    long countByBorrowerAndStatus(Borrower borrower, TransactionStatus status);
}
