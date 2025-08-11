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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingTransactionService {

    private final String borrowingNotFoundMsg = "Borrowing not found with ID: ";
    private final String bookNotFoundMsg = "Book not found with ID: ";
    private final String borrowerNotFoundMsg = "Borrower not found with ID: ";

    private final BorrowingTransactionRepository borrowingTransactionRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public BorrowingTransactionService(BorrowingTransactionRepository borrowingTransactionRepository, BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.borrowingTransactionRepository = borrowingTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
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

        // Update reverse sides of the relationship (borrower and book)

        borrower.getTransactions().add(newBorrowingTransaction);
        borrowerRepository.save(borrower);

        requestedBook.getTransactions().add(newBorrowingTransaction);
        bookRepository.save(requestedBook);

        // Persist

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
        Book newBook = bookRepository.findById(borrowingTransactionUpdateDTO.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(bookNotFoundMsg + borrowingTransactionUpdateDTO.getBookId()));

        Borrower newBorrower = borrowerRepository.findById(borrowingTransactionUpdateDTO.getBorrowerId())
                .orElseThrow(() -> new EntityNotFoundException(borrowerNotFoundMsg + borrowingTransactionUpdateDTO.getBorrowerId()));

        LocalDate newBorrowDate = borrowingTransactionUpdateDTO.getBorrowDate();

        LocalDate newReturnDate = borrowingTransactionUpdateDTO.getReturnDate();

        TransactionStatus newStatus = borrowingTransactionUpdateDTO.getStatus();

        // Fetch transaction
        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing transaction not found"));

        // Fetch old borrower
        Borrower oldBorrower = transaction.getBorrower();
//                borrowerRepository.findById(transaction.getBorrower().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));

        // Fetch old book
        Book oldBook = transaction.getBook();
//                bookRepository.findById(transaction.getBook().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Check book availability (only if changing book and new status is BORROWED)
        if (!newBook.equals(oldBook) &&
                newStatus == TransactionStatus.BORROWED &&
                !newBook.isAvailable()) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        // Status constraints
        if (transaction.getStatus() == TransactionStatus.RETURNED &&
                newStatus == TransactionStatus.BORROWED) {
            throw new IllegalStateException("Cannot change status from RETURNED to BORROWED");
        }
        if (newStatus == TransactionStatus.RETURNED) {
            oldBook.setAvailable(true); // Mark book as available
        }

        // Check borrow date validity
        if (newBorrowDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        // Check return date validity
        if (newReturnDate != null &&
                newReturnDate.isBefore(newBorrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        // Reverse-side updates for borrower
        if(!oldBorrower.equals(newBorrower)) {
            oldBorrower.getTransactions().remove(transaction);
            borrowerRepository.save(oldBorrower);
            newBorrower.getTransactions().add(transaction);
            transaction.setBorrower(newBorrower);
        }

        // Reverse-side updates for book
        if(!newBook.equals(oldBook)) {
            oldBook.getTransactions().remove(transaction);
            bookRepository.save(oldBook);
            newBook.getTransactions().add(transaction);
            transaction.setBook(newBook);
        }

        // Update fields
        transaction.setBorrower(newBorrower);
        transaction.setBook(newBook);
        transaction.setStatus(newStatus);
        transaction.setBorrowDate(newBorrowDate);
        transaction.setReturnDate(newReturnDate);

        borrowingTransactionRepository.save(transaction);
        borrowerRepository.save(newBorrower);
        bookRepository.save(newBook);

//        return borrowingTransactionMapper.toResponseDTO(transaction);
        return new BorrowingTransactionResponseDTO(
                transaction.getId(),
                transaction.getBook().getId(),
                transaction.getBorrower().getId(),
                transaction.getBorrowDate(),
                transaction.getReturnDate(),
                transaction.getStatus()
        );
    }

    public void deleteAllBorrowings(){
        List<BorrowingTransaction> borrowingTransactions = borrowingTransactionRepository.findAll();
        for(BorrowingTransaction borrowingTransaction : borrowingTransactions) {
            borrowingTransaction.getBook().getTransactions().remove(borrowingTransaction);
            borrowingTransaction.getBorrower().getTransactions().remove(borrowingTransaction);
        }
        borrowingTransactionRepository.deleteAll();
    }

    public void deleteBorrowingById(UUID id){
        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing transaction not found"));
        transaction.getBook().getTransactions().remove(transaction);
        transaction.getBorrower().getTransactions().remove(transaction);
        borrowingTransactionRepository.deleteById(id);
    }


}
