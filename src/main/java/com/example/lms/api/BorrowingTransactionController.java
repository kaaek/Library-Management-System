package com.example.lms.api;

import com.example.lms.dto.borrowings.BorrowingTransactionRequestDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionResponseDTO;
import com.example.lms.dto.borrowings.BorrowingTransactionUpdateDTO;
import com.example.lms.service.BorrowingTransactionService;
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
    public BorrowingTransactionResponseDTO createBorrowing(@RequestBody BorrowingTransactionRequestDTO borrowingTransactionRequestDTO){
        return borrowingTransactionService.createBorrowing(borrowingTransactionRequestDTO);
    }

    @GetMapping("/all")
    public List<BorrowingTransactionResponseDTO> getAllBorrowings(){
        return borrowingTransactionService.getAllBorrowings();
    }

    @GetMapping("/{id}")
    public BorrowingTransactionResponseDTO getBorrowingById(@PathVariable UUID id){
        return borrowingTransactionService.getBorrowingById(id);
    }

    @PutMapping("/{id}")
    public BorrowingTransactionResponseDTO updateBorrowing(@PathVariable UUID id, @RequestBody BorrowingTransactionUpdateDTO borrowingTransactionUpdateDTO) {
        return borrowingTransactionService.updateBorrowing(id, borrowingTransactionUpdateDTO);
    }

    @DeleteMapping("/all")
    public void deleteAllBorrowings(){
        borrowingTransactionService.deleteAllBorrowings();
    }

    @DeleteMapping("/{id}")
    public void deleteBorrowingById(@PathVariable UUID id){
        borrowingTransactionService.deleteBorrowingById(id);
    }
}
