package com.example.lms.contoller;

import com.example.lms.dto.borrower.BorrowerRequestDTO;
import com.example.lms.dto.borrower.BorrowerResponseDTO;
import com.example.lms.dto.borrower.BorrowerUpdateDTO;
import com.example.lms.service.BorrowerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping("/new")
    public ResponseEntity<BorrowerResponseDTO> createBorrower(@RequestBody BorrowerRequestDTO borrowerRequestDTO) {
        BorrowerResponseDTO dto = borrowerService.createBorrower(borrowerRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BorrowerResponseDTO>> getAllBorrowers() {
        List<BorrowerResponseDTO> dtos = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerResponseDTO> getBorrowerById(@PathVariable UUID id){
        BorrowerResponseDTO dto = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowerResponseDTO> update(@PathVariable UUID id, @RequestBody BorrowerUpdateDTO borrowerUpdateDTO) {
        BorrowerResponseDTO dto = borrowerService.update(id, borrowerUpdateDTO);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBorrower(@PathVariable UUID id){
        borrowerService.deleteBorrowerById(id);
        return ResponseEntity.ok("Borrower with id " + id + " was deleted");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAll(){
        borrowerService.deleteAllBorrowers();
        return ResponseEntity.ok("Borrowers were flushed successfully.");
    }

}
