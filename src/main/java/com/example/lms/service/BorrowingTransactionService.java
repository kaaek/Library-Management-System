package com.example.lms.service;

import com.example.lms.dto.borrowings.BorrowingTransactionRequestDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionResponseDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionUpdateDTO;
import com.example.lms.dto.email.EmailRequest;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.exception.MaxBorrowingsException;
import com.example.lms.model.Book;
import com.example.lms.model.Borrower;
import com.example.lms.model.BorrowingTransaction;
import com.example.lms.model.enums.TransactionStatus;
import com.example.lms.repository.BookRepository;
import com.example.lms.repository.BorrowerRepository;
import com.example.lms.repository.BorrowingTransactionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.modelmapper.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingTransactionService {

    @Value("${borrower.transaction.limit}")
    private int transactionLimit;

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowingTransactionRepository borrowingTransactionRepository; 
    private final EmailClient emailClient;
    private ModelMapper mapper;

    public BorrowingTransactionService(BorrowingTransactionRepository borrowingTransactionRepository, BookRepository bookRepository, BorrowerRepository borrowerRepository,
                                            ModelMapper mapper, EmailClient emailClient) {
        this.borrowingTransactionRepository = borrowingTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.mapper = mapper;
        this.emailClient = emailClient;
    }

    @Transactional
    public BorrowingTransactionResponseDTO createBorrowing(BorrowingTransactionRequestDTO borrowingTransactionRequestDTO){
        // Fields
        String isbn = borrowingTransactionRequestDTO.getIsbn().strip();
        String borrowerEmail = borrowingTransactionRequestDTO.getBorrowerEmail().strip().toLowerCase();

        // Fetch book
        Book requestedBook = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new EntityNotFoundException("Book with ISBN: "+ isbn + " was not found."));

        // Check if book is available
        if(!requestedBook.isAvailable()){
            throw new RuntimeException("Book with ISBN: " + isbn + " is unavailable for borrowing.");
        }

        // Fetch borrower
        Borrower borrower = borrowerRepository.findByEmail(borrowerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Borrower with e-mail: " + borrowerEmail + " was not found."));

        // Borrower must have at most 4 borrowings:
        long activeBorrowings = borrowingTransactionRepository.countByBorrowerAndStatus(borrower, TransactionStatus.BORROWED);
        if(activeBorrowings >= transactionLimit) { // Reject
            throw new MaxBorrowingsException("Borrower with e-mail: " + borrowerEmail + " has reached their borrowing limit, and cannot borrow more books.");
        }

        // Update book availability
        requestedBook.setAvailable(false);
        bookRepository.save(requestedBook);

        // Build transaction
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = borrowDate.plusMonths(1);
        TransactionStatus status = TransactionStatus.BORROWED;

        BorrowingTransaction newBorrowingTransaction = new BorrowingTransaction(
                requestedBook,
                borrower,
                borrowDate,
                returnDate,
                status
        );

        borrowingTransactionRepository.save(newBorrowingTransaction);

        // Send an e-mail notification
        sendEmail(borrowerEmail, requestedBook.getTitle());

        return mapper.map(newBorrowingTransaction, BorrowingTransactionResponseDTO.class);
    }

    public void sendEmail(String email, String bookTitle) {
        emailClient.sendEmail(new EmailRequest(email, "Book" + bookTitle + "borrowed successfully."));
    }

    public List<BorrowingTransactionResponseDTO> getAllBorrowings(){
        return borrowingTransactionRepository.findAll()
                .stream()
                .map(borrowingTransaction -> new BorrowingTransactionResponseDTO(
                        borrowingTransaction.getId(),
                        borrowingTransaction.getBook().getId(),
                        borrowingTransaction.getBorrower().getId(),
                        borrowingTransaction.getBorrowDate(),
                        borrowingTransaction.getReturnDate(),
                        borrowingTransaction.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public BorrowingTransactionResponseDTO getBorrowingById(UUID id) {
        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing with ID: " + id + " was not found."));

        return mapper.map(transaction, BorrowingTransactionResponseDTO.class);
    }

    public BorrowingTransactionResponseDTO updateBorrowing(UUID id, BorrowingTransactionUpdateDTO borrowingTransactionUpdateDTO) {

        // Fields
        String newEmail = borrowingTransactionUpdateDTO.getBorrowerEmail().strip().toLowerCase();
        Borrower newBorrower = borrowerRepository.findByEmail(newEmail)
                .orElseThrow(() -> new EntityNotFoundException("Borrower with e-mail: " + newEmail + " was not found."));

        LocalDate newBorrowDate = borrowingTransactionUpdateDTO.getBorrowDate();

        LocalDate newReturnDate = borrowingTransactionUpdateDTO.getReturnDate();

        TransactionStatus newStatus = borrowingTransactionUpdateDTO.getStatus();

        // Fetch transaction
        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing transaction not found"));

        // Fetch book
        Book book = transaction.getBook();

        // If changing the borrower:
        Borrower oldBorrower = transaction.getBorrower();
        if(!newBorrower.equals(oldBorrower)){
            long activeBorrowings = borrowingTransactionRepository.countByBorrowerAndStatus(newBorrower, TransactionStatus.BORROWED);
            if(activeBorrowings >= transactionLimit) { // Reject
                throw new MaxBorrowingsException("Borrower with e-mail: " + newEmail + " has reached their borrowing limit, and cannot borrow more books.");
            }
        }

        // Status constraints
        if (transaction.getStatus() == TransactionStatus.RETURNED &&
                newStatus == TransactionStatus.BORROWED) {
            throw new IllegalArgumentException("Cannot change status from RETURNED to BORROWED. Create a new transaction.");
        }

        // If returned, mark book as available
        if (newStatus == TransactionStatus.RETURNED) {
            book.setAvailable(true);
            bookRepository.save(book);
        }

        // Check borrow date validity
        if (newBorrowDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future.");
        }

        // Check return date validity
        if (newReturnDate != null &&
                newReturnDate.isBefore(newBorrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before the borrow date.");
        }

        // Update fields
        transaction.setBorrower(newBorrower);
        transaction.setStatus(newStatus);
        transaction.setBorrowDate(newBorrowDate);
        transaction.setReturnDate(newReturnDate);

        borrowingTransactionRepository.save(transaction);

        return mapper.map(transaction, BorrowingTransactionResponseDTO.class);
    }

    public void deleteAllBorrowings(){
        borrowingTransactionRepository.deleteAll();
    }

    public void deleteBorrowingById(UUID id){
            
        borrowingTransactionRepository.delete(borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing transaction not found")));
    }


}
