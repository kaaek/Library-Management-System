package com.example.lms.service;

import com.example.lms.dto.borrowings.BorrowingTransactionRequestDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionResponseDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionUpdateDTO;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.model.Book;
import com.example.lms.model.Borrower;
import com.example.lms.model.BorrowingTransaction;
import com.example.lms.model.enums.TransactionStatus;
import com.example.lms.repository.BookRepository;
import com.example.lms.repository.BorrowerRepository;
import com.example.lms.repository.BorrowingTransactionRepository;
import org.springframework.stereotype.Service;
import org.modelmapper.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingTransactionService {

    private final String borrowingNotFoundMsg = "Borrowing not found with ID: ";
    private final String bookNotFoundMsg = "Book not found with ID: ";
    private final String borrowerNotFoundMsg = "Borrower not found with ID: ";

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowingTransactionRepository borrowingTransactionRepository; 


    private ModelMapper mapper;

    public BorrowingTransactionService(BorrowingTransactionRepository borrowingTransactionRepository, BookRepository bookRepository, BorrowerRepository borrowerRepository, ModelMapper mapper) {
        this.borrowingTransactionRepository = borrowingTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.mapper = mapper;
    }

    public BorrowingTransactionResponseDTO createBorrowing(BorrowingTransactionRequestDTO borrowingTransactionRequestDTO){
        // Fields
        UUID bookId = borrowingTransactionRequestDTO.getBookId();
        UUID borrowerId = borrowingTransactionRequestDTO.getBorrowerId();

        // Fetch book
        Book requestedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(bookNotFoundMsg + bookId));

        // Check if book is available
        if(!requestedBook.isAvailable()){
            throw new RuntimeException("Book with ID: " + bookId + " is unavailable for borrowing.");
        }

        // Fetch borrower
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new EntityNotFoundException(borrowerNotFoundMsg + borrowerId));

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

        return new BorrowingTransactionResponseDTO(
                newBorrowingTransaction.getId(),
                newBorrowingTransaction.getBook().getId(),
                newBorrowingTransaction.getBorrower().getId(),
                newBorrowingTransaction.getBorrowDate(),
                newBorrowingTransaction.getReturnDate(),
                newBorrowingTransaction.getStatus()
        );
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
        BorrowingTransaction newBorrowingTransaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(borrowingNotFoundMsg + id));

        return new BorrowingTransactionResponseDTO(
                newBorrowingTransaction.getId(),
                newBorrowingTransaction.getBook().getId(),
                newBorrowingTransaction.getBorrower().getId(),
                newBorrowingTransaction.getBorrowDate(),
                newBorrowingTransaction.getReturnDate(),
                newBorrowingTransaction.getStatus()
        );
    }

    public BorrowingTransactionResponseDTO updateBorrowing(UUID id, BorrowingTransactionUpdateDTO borrowingTransactionUpdateDTO) {

        // Fields

        Borrower newBorrower = borrowerRepository.findById(borrowingTransactionUpdateDTO.getBorrowerId())
                .orElseThrow(() -> new EntityNotFoundException(borrowerNotFoundMsg + borrowingTransactionUpdateDTO.getBorrowerId()));

        LocalDate newBorrowDate = borrowingTransactionUpdateDTO.getBorrowDate();

        LocalDate newReturnDate = borrowingTransactionUpdateDTO.getReturnDate();

        TransactionStatus newStatus = borrowingTransactionUpdateDTO.getStatus();

        // Fetch transaction
        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing transaction not found"));

        // Fetch book
        Book book = transaction.getBook();

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
