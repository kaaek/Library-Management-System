package com.example.lms.service;

import com.example.lms.client.CardClient;
import com.example.lms.client.EmailClient;
import com.example.lms.dto.borrowings.BorrowingTransactionRequestDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionResponseDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionUpdateDTO;
import com.example.lms.dto.credit.CreditRequestDTO;
import com.example.lms.dto.debit.DebitRequestDTO;
import com.example.lms.dto.email.EmailRequest;
import com.example.lms.dto.transaction.TransactionResponseDTO;
import com.example.lms.exception.EntityNotFoundException;
import com.example.lms.exception.MaxBorrowingsException;
import com.example.lms.model.Borrower;
import com.example.lms.model.BorrowingTransaction;
import com.example.lms.model.Book.Book;
import com.example.lms.model.enums.Currency;
import com.example.lms.model.enums.TransactionStatus;
import com.example.lms.repository.BookRepository;
import com.example.lms.repository.BorrowerRepository;
import com.example.lms.repository.BorrowingTransactionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final CardClient cardClient;
    private ModelMapper mapper;

    public BorrowingTransactionService(BorrowingTransactionRepository borrowingTransactionRepository, BookRepository bookRepository, BorrowerRepository borrowerRepository,
                                            ModelMapper mapper, EmailClient emailClient, CardClient cardClient) {
        this.borrowingTransactionRepository = borrowingTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.mapper = mapper;
        this.emailClient = emailClient;
        this.cardClient = cardClient;
    }

    @Transactional
    public BorrowingTransactionResponseDTO createBorrowing(BorrowingTransactionRequestDTO borrowingTransactionRequestDTO){
        // DTO fields
        String isbn = borrowingTransactionRequestDTO.getIsbn().strip();
        String borrowerEmail = borrowingTransactionRequestDTO.getBorrowerEmail().strip().toLowerCase();
        TransactionStatus type = borrowingTransactionRequestDTO.getType(); // BORROWED or RETURNED
        LocalDate returnDate = borrowingTransactionRequestDTO.getReturnDate();
        String cardNumber = borrowingTransactionRequestDTO.getCardNumber();
        Currency currency = borrowingTransactionRequestDTO.getCurrency();
        
        // Other fields
        LocalDate borrowDate = LocalDate.now();
        LocalDate baseReturnDate = borrowDate.plusWeeks(1);

        // Fetch book
        Book requestedBook = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new EntityNotFoundException("Book with ISBN: "+ isbn + " was not found."));

        // Fetch borrower
        Borrower borrower = borrowerRepository.findByEmail(borrowerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Borrower with e-mail: " + borrowerEmail + " was not found."));

        BorrowingTransaction newBorrowingTransaction = new BorrowingTransaction();

        // Check transaction type
        if (type == TransactionStatus.BORROWED) {

            // Borrower must have at most 4 borrowings:
            long activeBorrowings = borrowingTransactionRepository.countByBorrowerAndStatus(borrower, TransactionStatus.BORROWED);
            if(activeBorrowings >= transactionLimit) { // Reject
                throw new MaxBorrowingsException("Borrower with e-mail: " + borrowerEmail + " has reached their borrowing limit, and cannot borrow more books.");
            }

            // Check if book is available to borrow
            if(!requestedBook.isAvailable()){
                throw new RuntimeException("Book with ISBN: " + isbn + " is unavailable for borrowing.");
            }

            // Get fees
            BigDecimal totalFee = calculatePrice(baseReturnDate, returnDate, requestedBook);
            
            // Create a debit request DTO
            DebitRequestDTO request = new DebitRequestDTO(cardNumber, totalFee, currency);
            ResponseEntity <TransactionResponseDTO> response = cardClient.debit(request);
            
            if(!response.getStatusCode().equals(HttpStatus.CREATED)) { // If not approved
                throw new IllegalStateException("Account invalid or funds are insufficient to borrow."); // TODO: Custom exceptions for exception handler.
            }

            // Record transaction and send an e-mail notification.
            // Update book availability
            requestedBook.setAvailable(false);
            bookRepository.save(requestedBook);
            
            // Build transaction
            TransactionStatus status = TransactionStatus.BORROWED;

            newBorrowingTransaction = new BorrowingTransaction(
                    requestedBook,
                    borrower,
                    borrowDate,
                    returnDate,
                    status
            );

            sendBorrowEmail(borrowerEmail, requestedBook.getTitle());
            
        } else if (type == TransactionStatus.RETURNED) { // RETURNED
            LocalDate currenDate = LocalDate.now();
            if(!returnDate.isAfter(currenDate)) {
                // Refund insurance fee
                BigDecimal insuranceFee = requestedBook.getProperties().getInsurance_fees();
                CreditRequestDTO request = new CreditRequestDTO(cardNumber, insuranceFee, currency);
                cardClient.credit(request);
            }
            // TODO: What if the borrower returned after the set returned date? (Settling extra fees)
            requestedBook.setAvailable(true);
            bookRepository.save(requestedBook);

            // Build transaction
            
            TransactionStatus status = TransactionStatus.RETURNED;

            newBorrowingTransaction = new BorrowingTransaction(
                    requestedBook,
                    borrower,
                    borrowDate,
                    returnDate,
                    status
            );

            sendReturnEmail(borrowerEmail, requestedBook.getTitle());
        }
        
        // // Check if book is available
        // if(!requestedBook.isAvailable()){
        //     throw new RuntimeException("Book with ISBN: " + isbn + " is unavailable for borrowing.");
        // }

        // // Fetch borrower
        // Borrower borrower = borrowerRepository.findByEmail(borrowerEmail)
        //         .orElseThrow(() -> new EntityNotFoundException("Borrower with e-mail: " + borrowerEmail + " was not found."));

        // // Borrower must have at most 4 borrowings:
        // long activeBorrowings = borrowingTransactionRepository.countByBorrowerAndStatus(borrower, TransactionStatus.BORROWED);
        // if(activeBorrowings >= transactionLimit) { // Reject
        //     throw new MaxBorrowingsException("Borrower with e-mail: " + borrowerEmail + " has reached their borrowing limit, and cannot borrow more books.");
        // }

        // // Update book availability
        // requestedBook.setAvailable(false);
        // bookRepository.save(requestedBook);

        // // Build transaction
        
        // TransactionStatus status = TransactionStatus.BORROWED;

        // BorrowingTransaction newBorrowingTransaction = new BorrowingTransaction(
        //         requestedBook,
        //         borrower,
        //         borrowDate,
        //         returnDate,
        //         status
        // );

        borrowingTransactionRepository.save(newBorrowingTransaction);

        // Send an e-mail notification
        sendBorrowEmail(borrowerEmail, requestedBook.getTitle());

        return mapper.map(newBorrowingTransaction, BorrowingTransactionResponseDTO.class);
    }

    public BigDecimal calculatePrice(LocalDate baseReturnDate, LocalDate returnDate, Book book) {

        BigDecimal extraDaysRentalPrice = book.getProperties().getExtra_days_rental_price();
        BigDecimal insuranceFees = book.getProperties().getInsurance_fees();
        BigDecimal basePrice = book.getBasePrice();

        BigDecimal extraDays = BigDecimal.valueOf(ChronoUnit.DAYS.between(baseReturnDate, returnDate));
        BigDecimal extraFees = extraDays.multiply(extraDaysRentalPrice);
        
        return basePrice.add(extraFees).add(insuranceFees);
    }

    public void sendBorrowEmail(String email, String bookTitle) {
        emailClient.sendEmail(new EmailRequest(email, "Book \"" + bookTitle + "\" borrowed successfully."));
    }

    public void sendReturnEmail(String email, String bookTitle) {
        emailClient.sendEmail(new EmailRequest(email, "Book \"" + bookTitle + "\" returned successfully."));
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

        BorrowingTransaction transaction = borrowingTransactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaction with ID: " + id + " not found in db."));

        // TODO: Sometimes deleting a transaction requires changing the availability of the mentioned book.
            
        borrowingTransactionRepository.delete(transaction);
    }


}
