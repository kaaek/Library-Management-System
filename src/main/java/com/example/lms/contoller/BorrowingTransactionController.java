package com.example.lms.contoller;

import com.example.lms.dto.borrowings.BorrowingTransactionRequestDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionResponseDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionUpdateDTO;
import com.example.lms.service.BorrowingTransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/borrowings")
public class BorrowingTransactionController {

    private final BorrowingTransactionService borrowingTransactionService;

    public BorrowingTransactionController(BorrowingTransactionService borrowingTransactionService) {
        this.borrowingTransactionService = borrowingTransactionService;
    }

    @PostMapping("/new")
    public ResponseEntity<BorrowingTransactionResponseDTO> createBorrowing(@RequestBody BorrowingTransactionRequestDTO borrowingTransactionRequestDTO){
        BorrowingTransactionResponseDTO dto = borrowingTransactionService.createBorrowing(borrowingTransactionRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BorrowingTransactionResponseDTO>> getAllBorrowings(){
        List<BorrowingTransactionResponseDTO> dtos = borrowingTransactionService.getAllBorrowings();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingTransactionResponseDTO> getBorrowingById(@PathVariable UUID id){
        BorrowingTransactionResponseDTO dto = borrowingTransactionService.getBorrowingById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowingTransactionResponseDTO> updateBorrowing(@PathVariable UUID id, @RequestBody BorrowingTransactionUpdateDTO borrowingTransactionUpdateDTO) {
        BorrowingTransactionResponseDTO dto = borrowingTransactionService.updateBorrowing(id, borrowingTransactionUpdateDTO);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllBorrowings(){
        borrowingTransactionService.deleteAllBorrowings();
        return ResponseEntity.ok("Transactions flushed successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBorrowingById(@PathVariable UUID id){
        borrowingTransactionService.deleteBorrowingById(id);
        return ResponseEntity.ok("Transaction with id " + id + " was deleted.");
    }
}
